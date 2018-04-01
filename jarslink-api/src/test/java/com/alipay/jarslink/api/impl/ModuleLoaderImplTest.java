/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.jarslink.api.impl;

import com.alipay.jarslink.api.Module;
import com.alipay.jarslink.api.ModuleLoader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.alipay.jarslink.api.impl.ModuleManagerTest.buildModuleConfig;

/**
 *
 * @author tengfei.fang
 * @version $Id: ModuleLoaderImplTest.java, v 0.1 2018年04月01日 12:12 PM tengfei.fang Exp $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/jarslink.xml"})
public class ModuleLoaderImplTest {

    @Autowired
    private ModuleLoader moduleLoader;

    @Test
    public void shouldLoadModule() {
        //1:加载模块
        Module module = loadModule();
        Assert.assertEquals("demo", module.getName());
        Assert.assertEquals("1.0.0.20170621", module.getVersion());
        Assert.assertEquals(3, module.getActions().size());
        Assert.assertNotNull(module);
        Assert.assertNotNull(module.getCreation());
        Assert.assertNotNull(module.getChildClassLoader());
        //卸载模块
        moduleLoader.unload(module);

        Assert.assertTrue(module.getActions().isEmpty());
        Assert.assertNotNull(module.getChildClassLoader());
    }

    @Test
    public void shouldLoadMultiModule() {
        for (int i = 0; i < 10; i++) {
            //1:加载模块
            String name = "demo" + i;
            Module module = loadModule(name);
            Assert.assertNotNull(module);
            //卸载模块
            moduleLoader.unload(module);
            Assert.assertTrue(module.getActions().isEmpty());
        }

    }

    private Module loadModule() {
        return moduleLoader.load(buildModuleConfig(true));
    }

    private Module loadModule(String name) {
        return moduleLoader.load(buildModuleConfig(name, true));
    }

}