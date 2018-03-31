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

import com.alipay.jarslink.api.*;
import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 模块加载和执行测试
 *
 * @author tengfei.fangtf
 * @version $Id: ModuleManagerTest.java
 * <p>
 * v 0.1 2017年06月20日 3:24 PM tengfei.fangtf Exp $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/jarslink.xml"})
public class ModuleManagerTest {

    @Autowired
    private ModuleManager moduleManager;

    @Autowired
    private ModuleLoader moduleLoader;

    @Test
    public void shouldLoadModule() {
        //1:加载模块
        Module module = loadModule("jarslink-module-demo-1.0.0.jar");
        Assert.assertNotNull(module);
        Assert.assertNotNull(module.getCreation());
        Assert.assertNotNull(module.getChildClassLoader());
        //卸载模块
        module.destroy();
        Assert.assertNotNull(module.getChildClassLoader());

    }

    @Test
    public void shouldRegisterModule() throws MalformedURLException {
        //2:注册模块
        Module module = loadModule("jarslink-module-demo-1.0.0.jar");
        Module removedModule = moduleManager.register(module);
        Assert.assertNull(removedModule);

        //3:查找模块
        Module findModule = moduleManager.find(module.getName());
        Assert.assertNotNull(findModule);

        Assert.assertNotNull(moduleManager.getErrorModuleContext());
        Assert.assertEquals(1, moduleManager.getModules().size());

        Module remove = moduleManager.remove(module.getName());

        Assert.assertNull(moduleManager.find(remove.getName()));
        Assert.assertEquals(0, moduleManager.getModules().size());
    }

    @Test
    public void shouldDoAction() {
        shouldDoAction(loadModule("jarslink-module-demo-1.0.0.jar"), "helloworld");
    }

    @Test
    public void shouldDoAnnotationAction() {
        //annotation方式加载，测试annotation中注入xml定义的action  同时测试annotation action和xmlaction同时加载
        ModuleConfig config = buildModuleConfig(true, "jarslink-module-demo-1.0.0.jar");
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

        //测试xml中和annotation注册同名bean（不能同时存在，如果有重名的情况下以xml中的为主）
        //4.2:查找和执行Action
        action = null;
        try {
            action = module.getAction("overrideXmlAction");
        } catch (Exception e) {
        }
        Assert.assertNull(action);
    }

    @Test
    public void shouldDoXmlAction() {
        //xml的方式加载
        ModuleConfig config = buildModuleConfig(true, "jarslink-module-demo-1.0.0.jar");

        Module module = moduleLoader.load(config);

        Assert.assertEquals(3, module.getActions().size());
        //4.2:查找annotation Action  因为xml没有配置该action所以不会被发现
        Action<String, String> action = null;
        try {
            action = module.getAction("annotationAction");
        } catch (NullPointerException e) {
            //由于找不到action所以会抛异常
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
    }

    /**
     * 构建模块配置信息
     */
    public static ModuleConfig buildModuleConfig(String moudulePath) {
        return buildModuleConfig(true, moudulePath);
    }

    public static ModuleConfig buildModuleConfig(boolean enabled, String moudulePath) {
        URL demoModule;
        ModuleConfig moduleConfig = new ModuleConfig();
        //通过该方法构建的配置都是使用注解形式扫描bean的
        String scanBase = "com.alipay.jarslink.main";
        moduleConfig.addScanPackage(scanBase);
        moduleConfig.removeScanPackage(scanBase);
        Map<String, Object> properties = new HashMap();
        moduleConfig.withEnabled(enabled).withVersion("1.0.0.20170621").withOverridePackages(ImmutableList.of("com" +
                ".alipay.jarslink.demo")).withProperties(properties);
        demoModule = Thread.currentThread().getContextClassLoader().getResource(moudulePath);

//        不允许突破双亲委派，如果允许需要修改Assert.assertEquals(result, "parent:hello");为Assert.assertEquals(result, "hello");
        moduleConfig.setOverridePackages(ImmutableList.of("com.alipay.jarslink.demo"));

        moduleConfig.setName("demo");
        moduleConfig.setEnabled(enabled);
        moduleConfig.setVersion("1.0.0.20170621");
        properties.put("url", "127.0.0.1");
        moduleConfig.setProperties(properties);
        moduleConfig.setModuleUrl(ImmutableList.of(demoModule));
        return moduleConfig;
    }

    private Module loadModule(String modulePath) {
        return moduleLoader.load(buildModuleConfig(true, modulePath));
    }

    private void shouldDoAction(Module module, String actionName) {
        moduleManager.register(module);
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
        moduleManager.remove(module.getName());
    }
}
