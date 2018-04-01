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

import com.alipay.jarslink.api.Module;
import com.alipay.jarslink.api.ModuleManager;
import com.alipay.jarslink.api.ModuleConfig;
import com.alipay.jarslink.api.ModuleLoader;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.transformValues;
import static com.alipay.jarslink.api.impl.ModuleUtil.destroyQuietly;

/**
 * 定时刷新模块
 *
 * @author tengfei.fangtf
 * @version $Id: AbstractModuleRefreshScheduler.java, v 0.1 2017年05月30日 2:55 PM tengfei.fangtf Exp $
 */
public abstract class AbstractModuleRefreshScheduler implements InitializingBean, DisposableBean, Runnable {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(AbstractModuleRefreshScheduler.class);

    /**
     * 默认延迟执行,单位秒
     */
    private static final int DEFAULT_INITIAL_DELAY = 5;

    /**
     * 模块刷新默认间隔,单位秒
     */
    private static final int DEFAULT_REFRESH_DELAY = 60;

    /** 初始化的延迟时间 */
    private int initialDelay = DEFAULT_INITIAL_DELAY;

    /** 刷新间隔时间 */
    private int refreshDelay = DEFAULT_REFRESH_DELAY;

    private ScheduledExecutorService scheduledExecutor;
    private ModuleManager            moduleManager;
    private ModuleLoader             moduleLoader;

    /**
     * 初始化ScheduledExecutor，启动定时任务，扫描数据库的ModuleConfig，并根据逻辑判断启动和卸载模块
     *
     * @see InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        //先刷新一次
        refreshModuleConfigs();
        scheduledExecutor = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("module_refresh-schedule-pool-%d").daemon(true).build());
        scheduledExecutor
                .scheduleWithFixedDelay(this, initialDelay, refreshDelay, TimeUnit.SECONDS);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("AbstractModuleRefreshScheduler start");
        }
    }

    /**
     * 关闭ScheduledExecutor
     * @see DisposableBean#destroy()
     */
    @Override
    public void destroy() throws Exception {
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdownNow();
        }
    }

    /**
     * ScheduledExecutor 定时运行的方法
     * @see Runnable#run()
     */
    @Override
    public void run() {
        try {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Start module configs refresh");
            }
            refreshModuleConfigs();
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Stop module configs refresh");
            }
        } catch (Throwable e) {
            LOGGER.error("Failed to refresh module configs", e);
        }
    }

    /**
     * 获取模块配置信息
     *
     * @return
     */
    public abstract List<ModuleConfig> queryModuleConfigs();

    /**
     * 刷新ModuleConfig
     */
    private void refreshModuleConfigs() {
        // 查找状态为ENABLED的ModuleConfig，并以模块名作为Key，放到Map中
        Map<String, ModuleConfig> moduleConfigs = indexModuleConfigByModuleName(filterEnabledModule());

        // 转换Map的Value，提取Module的Version，Map的Key为DataProvider，Value为Version
        Map<String, String> configVersions = transformToConfigVersions(moduleConfigs);
        // 获取当前内存中，也就是ModuleManager已经加载的模板版本，同样Map的Key为name，Value为Version
        Map<String, String> moduleVersions = transformToModuleVersions(moduleManager.getModules());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Config size: {}", configVersions.size());
            LOGGER.info("Module size: {}", moduleVersions.size());
            LOGGER.info("now in map {}", moduleVersions);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Config versions: {}", configVersions);
            LOGGER.debug("Module versions: {}", moduleVersions);
        }
        // 找出配置与当前内存里配置的不同
        MapDifference<String, String> difference = Maps.difference(configVersions, moduleVersions);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Version difference: {}", difference);
        }
        // 配置新增的
        putModules(moduleConfigs, configAdds(difference));
        // 配置版本与模块不同的
        putModules(moduleConfigs, configDifference(difference));
        // 模块多余的
        removeModules(modulesRedundant(difference));
    }

    /**
     * 查找状态为ENABLED的ModuleConfig
     */
    private Collection<ModuleConfig> filterEnabledModule() {
        List<ModuleConfig> moduleConfigs = queryModuleConfigs();
        if (moduleConfigs == null || moduleConfigs.isEmpty()) {
            return new ArrayList<>();
        }
        return Collections2.filter(moduleConfigs, new Predicate<ModuleConfig>() {
            @Override
            public boolean apply(ModuleConfig moduleConfig) {
                return moduleConfig.getEnabled();
            }
        });
    }

    /**
     * 根据dataProviders指定的ModuleConfig初始化模块，并放入ModuleManager中
     *
     * @param moduleConfigs
     * @param moduleNames
     */
    private void putModules(Map<String, ModuleConfig> moduleConfigs, Set<String> moduleNames) {
        for (String name : moduleNames) {
            ModuleConfig moduleConfig = moduleConfigs.get(name);
            try {
                if (isFailedVersion(moduleConfig)) {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("this version is failed, ignore.{}", moduleConfig);
                    }
                    continue;
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Load module config: {}", moduleConfig);
                }
                Module module = moduleLoader.load(moduleConfig);
                Module removed = moduleManager.register(module);
                destroyQuietly(removed);
                moduleManager.getErrorModuleContext().remove(name.toUpperCase(Locale.CHINESE));
                moduleManager.getErrorModuleContext().remove(name.toUpperCase(Locale.CHINESE) + "_ERROR");
            } catch (Exception e) {
                moduleManager.getErrorModuleContext().put(name.toUpperCase(Locale.CHINESE) + "_ERROR",
                        ToStringBuilder.reflectionToString(e));
                moduleManager.getErrorModuleContext().put(name.toUpperCase(Locale.CHINESE),
                        moduleConfig.getVersion());
                LOGGER.error("Failed to load module config: " + moduleConfig, e);
            } catch (Error e) {
                LOGGER.error("Failed to load module config: " + moduleConfig, e);
            }
        }
    }

    /**
     *
     * @param moduleConfig
     * @return
     */
    private boolean isFailedVersion(ModuleConfig moduleConfig) {
        checkNotNull(moduleConfig, "moduleConfig is null");
        String name = moduleConfig.getName();
        checkNotNull(name, "name is null");
        String version = moduleManager.getErrorModuleContext().get(name.toUpperCase(Locale.CHINESE));
        return moduleConfig.getVersion().equals(version);
    }

    /**
     * 移除并且卸载模块
     *
     * @param modulesRedundant
     */
    private void removeModules(Set<String> modulesRedundant) {
        for (String moduleName : modulesRedundant) {
            Module removed = moduleManager.remove(moduleName);
            destroyQuietly(removed);
        }
    }

    /**
     * 根据对比的结果，查找多余的模块，
     *
     * @param difference
     * @return
     */
    private Set<String> modulesRedundant(MapDifference<String, String> difference) {
        return difference.entriesOnlyOnRight().keySet();
    }

    /**
     * 根据对比结果，查找版本不同的模块
     *
     * @param difference
     * @return
     */
    private Set<String> configDifference(MapDifference<String, String> difference) {
        return difference.entriesDiffering().keySet();
    }

    /**
     * 根据对比结果，查找新增的模块
     *
     * @param difference
     * @return
     */
    private Set<String> configAdds(MapDifference<String, String> difference) {
        return difference.entriesOnlyOnLeft().keySet();
    }

    /**
     * 将一个Module List，转换为Map，Key为 name，Value为Version
     *
     * @param modules
     * @return
     */
    private Map<String, String> transformToModuleVersions(List<Module> modules) {
        return ImmutableMap.copyOf(transformValues(
                Maps.uniqueIndex(modules, new Function<Module, String>() {
                    @Override
                    public String apply(Module input) {
                        return input.getName();
                    }
                }), new Function<Module, String>() {
                    @Override
                    public String apply(Module input) {
                        return input.getVersion();
                    }
                }));
    }

    /**
     * 提取Map中Value的Version，转换成新的Map，Key为name，Value为Version
     *
     * @param moduleConfigs
     * @return
     */
    private Map<String, String> transformToConfigVersions(Map<String, ModuleConfig> moduleConfigs) {
        return ImmutableMap.copyOf(transformValues(moduleConfigs,
                new Function<ModuleConfig, String>() {
                    @Override
                    public String apply(ModuleConfig input) {
                        return input.getVersion();
                    }
                }));
    }

    /**
     * 将ModuleConfig List转换成为Map，Key为name，Value为ModuleConfig
     *
     * @param list
     * @return
     */
    private Map<String, ModuleConfig> indexModuleConfigByModuleName(Collection<ModuleConfig> list) {

        return ImmutableMap.copyOf(Maps.uniqueIndex(list, new Function<ModuleConfig, String>() {
            @Override
            public String apply(ModuleConfig input) {
                return input.getName();
            }
        }));
    }

    @Required
    public void setModuleManager(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    @Required
    public void setModuleLoader(ModuleLoader moduleLoader) {
        this.moduleLoader = moduleLoader;
    }

    public void setInitialDelay(int initialDelay) {
        this.initialDelay = initialDelay;
    }

    public void setRefreshDelay(int refreshDelay) {
        this.refreshDelay = refreshDelay;
    }

}
