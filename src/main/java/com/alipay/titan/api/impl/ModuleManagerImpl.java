package com.alipay.titan.api.impl;

import com.alipay.titan.api.Module;
import com.alipay.titan.api.ModuleManager;
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
 *
 */
public class ModuleManagerImpl implements ModuleManager, DisposableBean {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ModuleManagerImpl.class);

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
        return ImmutableList
                .copyOf(filter(modules.values(), instanceOf(SpringModule.class)));
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