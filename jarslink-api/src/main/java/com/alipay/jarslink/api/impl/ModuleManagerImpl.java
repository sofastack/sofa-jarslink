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
import com.alipay.jarslink.api.ModuleListener;
import com.alipay.jarslink.api.ModuleManager;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import java.util.ArrayList;
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
 * @version $Id: ModuleManagerImpl.java, v 0.1 Mar 20, 2017 4:04:32 PM tengfei.fangtf Exp $

 */
public class ModuleManagerImpl implements ModuleManager, DisposableBean {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ModuleManagerImpl.class);

    /**
     * 已注册的所有模块,key:moduleName upperCase
     */
    private final ConcurrentHashMap<String, RuntimeModule> allModules = new ConcurrentHashMap<String, RuntimeModule>();
    
    private List<ModuleListener> listeners;

    private RuntimeModule getRuntimeModule(String name) {
        RuntimeModule runtimeModule = allModules.get(name.toUpperCase());
        return runtimeModule != null ? runtimeModule : new RuntimeModule();
    }

    @Override
    public List<Module> getModules() {
        List<Module> modules = Lists.newArrayList();

        for (String name : allModules.keySet()) {
            RuntimeModule runtimeModule = getRuntimeModule((String) name);
            for (String version : runtimeModule.getModules().keySet()) {
                modules.add(runtimeModule.getModules().get(version));
            }
        }

        return ImmutableList
                .copyOf(filter(modules, instanceOf(SpringModule.class)));
    }

    @Override
    public Module find(String name) {
        checkNotNull(name, "module name is null");
        String defaultVersion = getDefaultVersion(name);
        checkNotNull(defaultVersion, "module default version is null");
        return find(name, defaultVersion);
    }

    private String getDefaultVersion(String name) {return getRuntimeModule((String) name).getDefaultVersion();}

    @Override
    public Module find(String name, String version) {
        checkNotNull(name, "module name is null");
        checkNotNull(version, "module version is null");
        return getRuntimeModule((String) name).getModule(version);
    }

    @Override
    public void activeVersion(String name, String version) {
        checkNotNull(name, "module name is null");
        checkNotNull(version, "module version is null");
        getRuntimeModule((String) name).setDefaultVersion(version);
    }

    @Override
    public String getActiveVersion(String name) {
        checkNotNull(name, "module name is null");
        return getDefaultVersion(name);
    }

    @Override
    public Module register(Module module) {
        checkNotNull(module, "module is null");
        String name = module.getName();
        String version = module.getVersion();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("register Module: {}-{}", name, version);
        }
        //same module and same version can not register
        Module registeredModule = getRuntimeModule(name).getModule(version);
        if (registeredModule != null) {
        	if (LOGGER.isInfoEnabled()) {
                LOGGER.info("not can register Module: {}-{}, destroy it.", name, version);
            }
        	onDestroyModule(registeredModule);
        	module.destroy();
            return null;
        }
        RuntimeModule runtimeModule = getRuntimeModule(name);
        Module oldModule = null;
        //module frist register
        if (runtimeModule.getModules().isEmpty()) {
            runtimeModule = new RuntimeModule().withName(name).withDefaultVersion(version).addModule(module);
            allModules.put(name.toUpperCase(), runtimeModule);
            onModuleLoaded(module);
        } else {
            //the same module to register again
            oldModule = runtimeModule.getDefaultModule();
            runtimeModule.addModule(module).setDefaultVersion(version);
            // remove module old version
            if (oldModule != null && module.getModuleConfig().isNeedUnloadOldVersion() && !runtimeModule.getModules().isEmpty()) {
                runtimeModule.getModules().remove(oldModule.getVersion());
                //移除旧模块后销毁旧模块
                onDestroyModule(oldModule);
                oldModule.destroy();
            }
        }
        return oldModule;
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
        Module module = getRuntimeModule((String) name).getModules().remove(version);
        if(module != null) {
        	//移除模块后销毁模块
        	onDestroyModule(module);
        	module.destroy();
        }
        return module;
    }

    @Override
    public void destroy() throws Exception {
        for (Module each : getModules()) {
            try {
            	onModuleLoaded(each);
                each.destroy();
            } catch (Exception e) {
                LOGGER.error("Failed to destroy module: " + each.getName(), e);
            }
        }
        allModules.clear();
    }

    @Override
    public Map<String, String> getErrorModuleContext() {
        Map<String, String> result = Maps.newHashMap();
        for (String name : allModules.keySet()) {
            RuntimeModule runtimeModule = getRuntimeModule((String) name);
            result.put(name, runtimeModule.getErrorContext());
        }
        return result;
    }
    
    /**
     * 批量设置模块生命周期监听，方便使用spring注入监听
     * @param listeners
     */
    public void setListeners(List<ModuleListener> listeners) {
    	if(listeners == null) {
    		throw new IllegalArgumentException("listeners is must.");
    	}
    	for(ModuleListener listener : listeners) {
    		addListener(listener);
    	}
    }

	@Override
	public void addListener(ModuleListener listener) {
		if(listeners == null) {
			synchronized (this) {
				if(listeners == null) {
					listeners = new ArrayList<ModuleListener>(5);
				}
			}
		}
		if(!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	@Override
	public void removeListener(ModuleListener listener) {
		if(listeners != null) {
			listeners.remove(listener);
		}
	}
	
	/**
	 * 模块加载完成后调用监听
	 * @param module
	 */
	private void onModuleLoaded(Module module) {
		if(listeners != null) {
			for(ModuleListener listener : listeners) {
				listener.onLoaded(module);
			}
		}
	}
	
	/**
	 * 模块销毁前调用监听
	 * @param module
	 */
	private void onDestroyModule(Module module) {
		if(listeners != null) {
			for(ModuleListener listener : listeners) {
				listener.onPreDestroy(module);
			}
		}
	}
}