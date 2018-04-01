/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.jarslink.api.impl;

import com.alipay.jarslink.api.Module;
import com.alipay.jarslink.api.ToStringObject;

import java.util.concurrent.ConcurrentHashMap;

/**
 * module in runtime
 *
 * @author tengfei.fang
 * @version $Id: RuntimeModule.java, v 0.1 2018年04月01日 12:25 AM tengfei.fang Exp $
 */
public class RuntimeModule extends ToStringObject {

    private String name;

    private String defaultVersion;

    /**
     * load module error msg
     */
    private String errorContext;

    /**
     * all version module,key:version
     */
    private ConcurrentHashMap<String, Module> modules = new ConcurrentHashMap();

    public Module getModule(String version) {
        return modules.get(version);
    }

    public Module getDefaultModule() {
        return modules.get(getDefaultVersion());
    }

    public RuntimeModule addModule(Module module) {
        modules.put(module.getVersion(), module);
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RuntimeModule withName(String name) {
        this.name = name;
        return this;
    }

    public String getDefaultVersion() {
        return defaultVersion;
    }

    public void setDefaultVersion(String defaultVersion) {
        this.defaultVersion = defaultVersion;
    }

    public RuntimeModule withDefaultVersion(String defaultVersion) {
        setDefaultVersion(defaultVersion);
        return this;
    }

    public ConcurrentHashMap<String, Module> getModules() {
        return modules;
    }

    public String getErrorContext() {
        return errorContext;
    }

    public void setErrorContext(String errorContext) {
        this.errorContext = errorContext;
    }

}