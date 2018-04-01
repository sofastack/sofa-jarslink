/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.jarslink.api.impl;

import com.alipay.jarslink.api.Module;
import com.alipay.jarslink.api.ModuleConfig;
import com.alipay.jarslink.api.ModuleLoader;
import com.alipay.jarslink.api.ModuleManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URL;

/**
 * suport load multi-version of the module
 *
 * @author tengfei.fang
 * @version $Id: ModuleManagerMultiVersionTest.java, v 0.1 2018年03月31日 11:16 PM tengfei.fang Exp $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/jarslink.xml"})
public class ModuleManagerMultiVersionTest {

    private static final String DEMO_MODULE = "demo";

    @Autowired
    private ModuleLoader moduleLoader;

    @Autowired
    private ModuleManager moduleManager;

    void registerModule() {
        Module firstVersionModule = loadModule("1.0");
        Module secondVersionModule = loadModule("2.0");

        moduleManager.register(firstVersionModule);
        moduleManager.register(secondVersionModule);
    }

    void removeModule() {
        moduleManager.remove(DEMO_MODULE, "1.0");
        moduleManager.remove(DEMO_MODULE, "2.0");
    }

    @Test
    public void shouldAddMultiVersionModule() {
        registerModule();
        Module demo1 = moduleManager.find(DEMO_MODULE, "1.0");
        Assert.assertNotNull(demo1);

        Module demo2 = moduleManager.find(DEMO_MODULE, "2.0");
        Assert.assertNotNull(demo2);

        Assert.assertEquals(2, moduleManager.getModules().size());

        removeModule();
    }

    @Test
    public void shouldActiveModuleVersion() {
        registerModule();
        moduleManager.activeVersion(DEMO_MODULE, "1.0");
        String activeVersion = moduleManager.getActiveVersion(DEMO_MODULE);
        Assert.assertEquals("1.0", activeVersion);

        Module module = moduleManager.find(DEMO_MODULE);
        Assert.assertEquals("1.0", module.getVersion());
        removeModule();
    }

    private Module loadModule(String moduleVersion) {
        return moduleLoader.load(buildModuleConfig(moduleVersion));
    }

    public static ModuleConfig buildModuleConfig(String moduleVersion) {
        URL module = Thread.currentThread().getContextClassLoader().getResource("jarslink-module-demo-1.0.0.jar");
        return new ModuleConfig().withName(DEMO_MODULE).withVersion(moduleVersion).addModuleUrl(module)
                .withNeedUnloadOldVersion(false);
    }

}