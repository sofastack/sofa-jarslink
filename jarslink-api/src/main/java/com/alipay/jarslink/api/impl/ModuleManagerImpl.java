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
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.Iterables.filter;

/**
 * 模块管理，包含获取模块，执行模块里的方法
 *
 * @author tengfei.fangtf
 *
 */
public class ModuleManagerImpl implements ModuleManager, DisposableBean {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ModuleManagerImpl.class);

    /**
     * 已注册的所有模块
     */
    private final ConcurrentHashMap<String, Module> allModules = new ConcurrentHashMap();

    /**
     * 每个模块的默认版本
     */
    private final ConcurrentHashMap<String, String> defaultVersions = new ConcurrentHashMap();

    /**
     * 加载模块错误信息
     */
    private final ConcurrentHashMap<String, String> errorContext = new ConcurrentHashMap();

    @Override
    public List<Module> getModules() {
        return ImmutableList
                .copyOf(filter(allModules.values(), instanceOf(SpringModule.class)));
    }

    @Override
    public Module find(String name) {
        checkNotNull(name, "module name is null");
        String defaultVersion = getDefaultVersion(name);
        checkNotNull(defaultVersion, "module default version is null");
        return find(name, defaultVersion);
    }

    private String getDefaultVersion(String name) {return defaultVersions.get(name.toUpperCase());}

    @Override
    public Module find(String name, String version) {
        return allModules.get(getModuleKey(name, version));
    }

    @Override
    public String activeVersion(String name, String version) {
        checkNotNull(name, "module name is null");
        checkNotNull(version, "module version is null");
        return defaultVersions.put(getModuleName(name), version);
    }

    private static String getModuleName(String name) {return name.toUpperCase();}

    public static String getModuleKey(String name, String version) {
        checkNotNull(name, "module name is null");
        checkNotNull(version, "module version is null");
        return getModuleName(name) + "_" + version;
    }

    @Override
    public Module register(Module module) {
        checkNotNull(module, "module is null");
        String name = module.getName();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Put Module: {}-{}", name, module.getVersion());
        }

        //设置默认版本
        if (!defaultVersions.containsKey(getModuleName(name))) {
            activeVersion(name, module.getVersion());
        }

        return allModules.put(getModuleKey(name, module.getVersion()), module);
    }

    @Override
    public Module remove(String name) {
        checkNotNull(name, "module name is null");
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Remove Module: {}", name);
        }
        String defaultVersion = getDefaultVersion(name);
        return remove(name, defaultVersion);
    }

    @Override
    public Module remove(String name, String version) {
        return allModules.remove(getModuleKey(name, version));
    }

    @Override
    public void destroy() throws Exception {
        for (Module each : allModules.values()) {
            try {
                each.destroy();
            } catch (Exception e) {
                LOGGER.error("Failed to destroy module: " + each.getName(), e);
            }
        }
        allModules.clear();
        defaultVersions.clear();
        errorContext.clear();
    }

    @Override
    public Map<String, String> getErrorModuleContext() {
        return errorContext;
    }

}