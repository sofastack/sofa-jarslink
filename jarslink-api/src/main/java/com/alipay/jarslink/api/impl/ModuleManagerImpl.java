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
import com.alipay.jarslink.api.ModuleRuntimeException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.Iterables.filter;

/**
 * 模块管理，包含获取模块，执行模块里的方法
 *
 * @author tengfei.fangtf
 * @version $Id: ModuleManagerImpl.java, v 0.1 Mar 20, 2017 4:04:32 PM tengfei.fangtf Exp $
 */
public class ModuleManagerImpl implements ModuleManager, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleManagerImpl.class);

    /**
     * 已注册的所有模块,key:moduleName upperCase
     * <p>
     * 使用HashMap替换ConcurrentHashMap，内部使用ReentrantReadWriteLock控制并发逻辑
     */
    private final Map<String, RuntimeModule> allModules = new ConcurrentHashMap();
    /**
     * 操作allModules的资源锁
     */
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private RuntimeModule getRuntimeModule(String name) {
        RuntimeModule runtimeModule = allModules.get(name.toUpperCase());
        return runtimeModule != null ? runtimeModule : new RuntimeModule();
    }

    @Override
    public List<Module> getModules() {
        final List<Module> modules = Lists.newArrayList();
        for (Map.Entry<String, RuntimeModule> moduleEntry : allModules.entrySet()) {
            RuntimeModule runtimeModule = moduleEntry.getValue();
            modules.addAll(runtimeModule.getModules().values());
        }
        return ImmutableList.copyOf(filter(modules, instanceOf(SpringModule.class)));
    }

    @Override
    public Module find(String name) {
        checkNotNull(name, "module name is null");
        RuntimeModule runtimeModule = getRuntimeModule(name);
        String defaultVersion = runtimeModule.getDefaultVersion();
        checkNotNull(defaultVersion, "module default version is null");
        return runtimeModule.getModule(defaultVersion);
    }

    private String getDefaultVersion(String name) {
        return getRuntimeModule(name).getDefaultVersion();
    }

    @Override
    public Module find(String name, String version) {
        checkNotNull(name, "module name is null");
        checkNotNull(version, "module version is null");
        return getRuntimeModule(name).getModule(version);
    }

    @Override
    public void activeVersion(String name, String version) {
        checkNotNull(name, "module name is null");
        checkNotNull(version, "module version is null");
        getRuntimeModule(name).setDefaultVersion(version);
    }

    @Override
    public String getActiveVersion(String name) {
        checkNotNull(name, "module name is null");
        return getDefaultVersion(name);
    }

    @Override
    public Module register(Module module) {
        checkNotNull(module, "module is null");
        final String name = module.getName();
        final String version = module.getVersion();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("register Module: {}-{}", name, version);
        }

        //此处如果不加读锁，那么多线程情况下有可能同时进入runtimeModule.getModules().isEmpty()，此时多个线程
        //将同时调用allModules.put(name.toUpperCase(), runtimeModule)，而由于key相同，此时只有一个线程会成功，而
        //失败的线程也无法得知自己已经失败
        return writeExec(new Callable<Module>() {
            @Override
            public Module call() {
                //如果当前系统存在该模块那么应该抛出异常，否则外部调用无法得知是否注册成功
                ModuleManagerImpl.this.checkDuplicate(name, version);

                RuntimeModule runtimeModule = getRuntimeModule(name);
                Module oldModule = null;
                //module frist register
                if (runtimeModule.getModules().isEmpty()) {
                    runtimeModule = new RuntimeModule().withName(name).withDefaultVersion(version).addModule(module);
                    allModules.put(name.toUpperCase(), runtimeModule);
                } else {
                    //the same module to register again
                    oldModule = runtimeModule.getDefaultModule();
                    runtimeModule.addModule(module).setDefaultVersion(version);
                    // remove module old version
                    if (oldModule != null && module.getModuleConfig().isNeedUnloadOldVersion()) {
                        runtimeModule.getModules().remove(oldModule.getVersion());
                    }
                }
                return oldModule;
            }
        });
    }


    @Override
    public Module remove(String name) {
        checkNotNull(name, "module name is null");
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Remove Module: {}", name);
        }
        return remove(name, getRuntimeModule(name).getDefaultVersion());
    }

    @Override
    public Module remove(String name, String version) {
        checkNotNull(name, "module name is null");
        checkNotNull(version, "module version is null");
        return getRuntimeModule(name).getModules().remove(version);
    }

    @Override
    public void destroy() throws Exception {
        writeExec(new Callable<Object>() {
            @Override
            public Object call() {
                for (Module each : getModules()) {
                    try {
                        each.destroy();
                    } catch (Exception e) {
                        LOGGER.error("Failed to destroy module: " + each.getName(), e);
                    }
                }

                allModules.clear();
                return null;
            }
        });
    }

    @Override
    public Map<String, String> getErrorModuleContext() {
        final Map<String, String> result = Maps.newHashMap();
        for (String name : allModules.keySet()) {
            RuntimeModule runtimeModule = getRuntimeModule(name);
            result.put(name, runtimeModule.getErrorContext());
        }
        return result;
    }

    /**
     * 检查当前是否有指定模块的指定版本存在系统
     *
     * @param name    模块名
     * @param version 版本号
     * @throws ModuleRuntimeException 如果当前系统存在该模块的指定版本将抛出异常
     */
    private void checkDuplicate(String name, String version) throws ModuleRuntimeException {
        RuntimeModule runtimeModule = allModules.get(name.toUpperCase());
        runtimeModule = runtimeModule != null ? runtimeModule : new RuntimeModule();
        Module registeredModule = runtimeModule.getModule(version);
        if (registeredModule != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("duplicate module :[").append(name).append(":").append(version).append("]");
            throw new ModuleRuntimeException(sb.toString());
        }
    }

    /**
     * 对allModules的写操作
     *
     * @param operation 操作
     * @param <T>       操作返回值类型
     * @return 操作结果
     */
    private <T> T writeExec(Callable<T> operation) {
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            return operation.call();
        } catch (Exception e) {
            throw new ModuleRuntimeException("writeExec error", e);
        } finally {
            writeLock.unlock();
        }
    }
}