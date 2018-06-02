/*
 *
 *  * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package com.alipay.jarslink.api.impl;

import com.alipay.jarslink.api.Action;
import com.alipay.jarslink.api.Module;
import com.alipay.jarslink.api.ModuleConfig;
import com.alipay.jarslink.api.ModuleRuntimeException;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import sun.misc.ClassLoaderUtil;

import java.beans.Introspector;
import java.net.URLClassLoader;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * 集成Spring上下文的模块,从Spring上下中找Action
 *
 * @author tengfei.fangtf
 * @version $Id: SpringModule.java, v 0.1 2017年05月30日 2:55 PM tengfei.fangtf Exp $
 */
public class SpringModule implements Module {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringModule.class);

    /**
     * 模块的配置信息
     */
    ModuleConfig moduleConfig;

    /**
     * 模块的名称
     */
    private final String name;

    /**
     * 模块的版本
     */
    private final String version;

    /**
     * 模块启动的时间
     */
    private final Date creation;

    /**
     * 模块中的Action，Key为大写Action名称
     */
    private final Map<String, Action> actions;

    private final ConfigurableApplicationContext applicationContext;

    public SpringModule(ModuleConfig moduleConfig, String version, String name, ConfigurableApplicationContext
            applicationContext) {
        this.moduleConfig = moduleConfig;
        this.applicationContext = applicationContext;
        this.version = version;
        this.name = name;
        this.creation = new Date();
        Map<String, Action> actions = scanActions(applicationContext, Action.class, new Function<Action, String>() {
            @Override
            public String apply(Action input) {
                return input.getActionName();
            }
        });
        //转换为代理action
        this.actions = Maps.transformValues(actions, new Function<Action, Action>() {
            @Override
            public Action apply(Action input) {
                return new ActionProxy(input);
            }
        });
    }

    /**
     * 扫描模块里的ACTION
     *
     * @param applicationContext
     * @param type
     * @param keyFunction
     * @param <T>
     * @return
     */
    private <T> Map<String, T> scanActions(ApplicationContext applicationContext, Class<T> type, Function<T, String>
            keyFunction) {
        Map<String, T> actions = Maps.newHashMap();
        //find Action in module
        Collection<T> actionCollection = applicationContext.getBeansOfType(type).values();
        for (T action : actionCollection) {
            String actionName = keyFunction.apply(action);
            if (isBlank(actionName)) {
                throw new ModuleRuntimeException("JarsLink scanActions actionName is null");
            }
            String key = actionName.toUpperCase(Locale.CHINESE);
            checkState(!actions.containsKey(key), "Duplicated action %s found by: %s", type.getSimpleName(), key);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("JarsLink Scan action: {}: bean: {}", key, action);
            }
            actions.put(key, action);
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("JarsLink Scan actions finish: {}", ToStringBuilder.reflectionToString(actions));
        }
        return actions;
    }

    @Override
    public Map<String, Action> getActions() {
        return actions;
    }

    @Override
    public <R, T> Action<R, T> getAction(String actionName) {
        checkNotNull(actionName, "actionName is null");
        Action action = actions.get(actionName.toUpperCase());
        checkNotNull(action, "find action is null,actionName=" + actionName);
        return action;
    }

    @Override
    public <R, T> T doAction(String actionName, R actionRequest) {
        checkNotNull(actionName, "actionName is null");
        checkNotNull(actionRequest, "actionRequest is null");
        return (T) doActionWithinModuleClassLoader(getAction(actionName), actionRequest);
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * 调用Action处理请求，注意的是执行时应该用Action的ClassLoader
     *
     * @param action
     * @param actionRequest
     * @return
     */
    protected <R, T> T doActionWithinModuleClassLoader(Action<R, T> action, R actionRequest) {
        checkNotNull(action, "action is null");
        checkNotNull(actionRequest, "actionRequest is null");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader moduleClassLoader = action.getClass().getClassLoader();
            Thread.currentThread().setContextClassLoader(moduleClassLoader);
            return action.execute(actionRequest);
        } catch (Exception e) {
            LOGGER.error("调用模块出现异常,action=" + action, e);
            throw new ModuleRuntimeException("doActionWithinModuleClassLoader has error,action=" + action, e);
        } finally {
            Thread.currentThread().setContextClassLoader(classLoader);
        }
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public Date getCreation() {
        return creation;
    }

    @Override
    public void destroy() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Close application context: {}", applicationContext);
        }
        //destroy action
        clearAction();
        //close spring context
        closeQuietly(applicationContext);
        //clean classloader
        clear(applicationContext.getClassLoader());
    }

    /**
     * 销毁Action（如果不销毁Action对象，那么ClassLoader加载的该Action的Class就不会销毁，而Module有getAction方法有可能
     * 将Action的引用泄露，所以加一层代理来达到销毁Action对象的目的进而在ClassLoader卸载后能够正确的将Class卸载）
     */
    private void clearAction() {
        if (actions.isEmpty()) {
            return;
        }
        for (Action action : actions.values()) {
            if (action instanceof ActionProxy) {
                ((ActionProxy) action).destroy();
            }
        }
        actions.clear();
    }

    /**
     * 清除类加载器
     *
     * @param classLoader
     */
    public static void clear(ClassLoader classLoader) {
        checkNotNull(classLoader, "classLoader is null");
        Introspector.flushCaches();
        //从已经使用给定类加载器加载的缓存中移除所有资源包
        ResourceBundle.clearCache(classLoader);
        //Clear the introspection cache for the given ClassLoader
        CachedIntrospectionResults.clearClassLoader(classLoader);
        LogFactory.release(classLoader);
        if (classLoader instanceof URLClassLoader) {
            ClassLoaderUtil.releaseLoader((URLClassLoader) classLoader);
        }
    }

    /**
     * 关闭Spring上下文
     *
     * @param applicationContext
     */
    private static void closeQuietly(ConfigurableApplicationContext applicationContext) {
        checkNotNull(applicationContext, "applicationContext is null");
        try {
            applicationContext.close();
        } catch (Exception e) {
            LOGGER.error("Failed to close application context", e);
        }
    }

    @Override
    public ModuleConfig getModuleConfig() {
        return moduleConfig;
    }

    @Override
    public ClassLoader getChildClassLoader() {
        return this.applicationContext.getClassLoader();
    }

    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

}
