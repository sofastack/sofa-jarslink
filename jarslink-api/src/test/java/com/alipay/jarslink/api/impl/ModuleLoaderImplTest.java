/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.jarslink.api.impl;

import com.alipay.jarslink.api.Action;
import com.alipay.jarslink.api.Module;
import com.alipay.jarslink.api.ModuleConfig;
import com.alipay.jarslink.api.ModuleLoader;
import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author tengfei.fang
 * @version $Id: ModuleLoaderImplTest.java, v 0.1 2018年04月01日 12:12 PM tengfei.fang Exp $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/jarslink.xml"})
public class ModuleLoaderImplTest {

    public static final String JARSLINK_MODULE_DEMO = "jarslink-module-demo-1.0.0.jar";

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

    @Test
    public void shouldDoAction() {

        Module module = loadModule();
        String actionName = "helloworld";

        //4.1:查找和执行Action
        ModuleConfig moduleConfig = new ModuleConfig();
        moduleConfig.setName("h");
        moduleConfig.setEnabled(true);

        ModuleConfig result = module.doAction(actionName, moduleConfig);
        Assert.assertEquals(3, module.getActions().size());
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getName(), moduleConfig.getName());
        Assert.assertEquals(result.getEnabled(), moduleConfig.getEnabled());

        //4.2:查找和执行Action
        Action<ModuleConfig, ModuleConfig> action = module.getAction(actionName);
        Assert.assertNotNull(action);
        result = action.execute(moduleConfig);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getName(), moduleConfig.getName());
        Assert.assertEquals(result.getEnabled(), moduleConfig.getEnabled());

        //卸载模块
        moduleLoader.unload(module);
        Assert.assertTrue(module.getActions().isEmpty());
    }

    @Test
    public void shouldDoOverrideAction() {
        //测试注解action被xml中的action覆盖
        ModuleConfig config = buildModuleConfig(true);
        config.addScanPackage("com.alipay.jarslink.main").addScanPackage("com.alipay.jarslink.demo");

        Module module = moduleLoader.load(config);
        Action<String, String> action = null;
        try {
            action = module.getAction("overrideXmlAction");
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
        Assert.assertNull(action);
    }

    @Test
    public void shouldDoAnnotationAction() {
        //annotation方式加载，测试annotation中注入xml定义的action  同时测试annotation action和xmlaction同时加载
        ModuleConfig config = buildModuleConfig(true);
        config.addScanPackage("com.alipay.jarslink.main");
        config.addScanPackage("com.alipay.jarslink.demo");

        Module module = moduleLoader.load(config);
        Assert.assertEquals(4, module.getActions().size());

        //获取annotationAction  该action中注入xml中定义的xmlAction，使用xmlAction的实现
        String result = module.doAction("annotationAction", "hello");
        Assert.assertNotNull(result);
        //实际结果为xmlAction返回，annotationAction作为代理
        Assert.assertEquals(result, "xml:hello");

        //4.2:查找和执行Action
        Action<String, String> action = module.getAction("annotationAction");
        Assert.assertNotNull(action);
        result = action.execute("hello");
        Assert.assertNotNull(result);
        Assert.assertEquals(result, "xml:hello");

        //查找和执行xml bean
        result = module.doAction("xmlAction", "hello");
        Assert.assertNotNull(result);
        Assert.assertEquals(result, "xml:hello");

        //4.2:查找和执行Action
        action = module.getAction("xmlAction");
        Assert.assertNotNull(action);
        result = action.execute("hello");
        Assert.assertNotNull(result);
        Assert.assertEquals(result, "xml:hello");

        //卸载模块
        moduleLoader.unload(module);
        Assert.assertTrue(module.getActions().isEmpty());
    }

    @Test
    public void shouldDoXmlAction() {
        //xml的方式加载
        ModuleConfig config = buildModuleConfig(true);

        Module module = moduleLoader.load(config);
        Assert.assertEquals(3, module.getActions().size());
        //4.2:查找annotation Action  因为xml没有配置该action所以不会被发现
        Action<String, String> action = null;
        try {
            action = module.getAction("annotationAction");
        } catch (NullPointerException e) {
            //由于找不到action所以会抛异常
            Assert.assertNotNull(e);
        }
        Assert.assertNull(action);

        //查找和执行xml bean
        String result = module.doAction("xmlAction", "hello");
        Assert.assertNotNull(result);
        Assert.assertEquals(result, "xml:hello");

        //4.2:查找和执行Action
        action = module.getAction("xmlAction");
        Assert.assertNotNull(action);
        result = action.execute("hello");
        Assert.assertNotNull(result);
        Assert.assertEquals(result, "xml:hello");

        //卸载模块
        moduleLoader.unload(module);
        Assert.assertTrue(module.getActions().isEmpty());
    }

    /**
     * 构建模块配置信息
     */
    public static ModuleConfig buildModuleConfig() {
        return buildModuleConfig(true);
    }

    public static ModuleConfig buildModuleConfig(boolean enabled) {
        return buildModuleConfig("demo", "1.0.0.20170621", enabled);
    }

    public static ModuleConfig buildModuleConfig(String name, boolean enabled) {
        return buildModuleConfig(name, "1.0.0.20170621", enabled);
    }

    public static ModuleConfig buildModuleConfig(String name, String version, boolean enabled) {
        URL demoModule;
        ModuleConfig moduleConfig = new ModuleConfig();
        //通过该方法构建的配置都是使用注解形式扫描bean的
        String scanBase = "com.alipay.jarslink.main";
        moduleConfig.addScanPackage(scanBase);
        moduleConfig.removeScanPackage(scanBase);
        Map<String, Object> properties = new HashMap();
        moduleConfig.withEnabled(enabled).withVersion("1.0.0.20170621").withOverridePackages(ImmutableList.of(
                "com.alipay.jarslink.demo")).withProperties(properties);
        demoModule = Thread.currentThread().getContextClassLoader().getResource(JARSLINK_MODULE_DEMO);

        moduleConfig.setOverridePackages(ImmutableList.of("com.alipay.jarslink.demo"));

        moduleConfig.setName(name);
        moduleConfig.setEnabled(enabled);
        moduleConfig.setVersion("1.0.0.20170621");
        properties.put("url", "127.0.0.1");
        moduleConfig.setProperties(properties);
        moduleConfig.setModuleUrl(ImmutableList.of(demoModule));
        return moduleConfig;
    }

    private Module loadModule() {
        return moduleLoader.load(buildModuleConfig(true));
    }

    private Module loadModule(String name) {
        return moduleLoader.load(buildModuleConfig(name, "1.0.0.20170621", true));
    }

}