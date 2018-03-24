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
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.Iterables.filter;

/**
 * 模块管理，包含获取模块，执行模块里的方法
 *
 * @author tengfei.fangtf
 */
public class ModuleManagerImpl implements ModuleManager, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleManagerImpl.class);

    /**
     * 运行时模块,模块名:模块对象
     */
    private final ConcurrentHashMap<String, Module> modules = new ConcurrentHashMap();

    /**
     * 加载模块错误信息
     */
    private final ConcurrentHashMap<String, String> errorContext = new ConcurrentHashMap();

    @Override
    public List<Module> getModules() {
        return ImmutableList.copyOf(filter(modules.values(), instanceOf(SpringModule.class)));
    }

    @Override
    public Module find(String name) {
        checkNotNull(name, "module name is null");
        return modules.get(name.toUpperCase());
    }

    @Override
    public Module register(Module module) {
        checkNotNull(module, "module is null");
        String name = module.getName();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Put Module: {}-{}", name, module.getVersion());
        }

        return modules.put(name.toUpperCase(Locale.CHINESE), module);
    }

    @Override
    public Module remove(String name) {
        checkNotNull(name, "module name is null");
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Remove Module: {}", name);
        }
        return modules.remove(name.toUpperCase());
    }

    @Override
    public void destroy() throws Exception {
        for (Module each : modules.values()) {
            try {
                each.destroy();
            } catch (Exception e) {
                LOGGER.error("Failed to destroy module: " + each.getName(), e);
            }
        }
        modules.clear();
    }

    @Override
    public Map<String, String> getErrorModuleContext() {
        return errorContext;
    }

}