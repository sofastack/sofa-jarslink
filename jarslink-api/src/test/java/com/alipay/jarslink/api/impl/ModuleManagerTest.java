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

import com.alipay.jarslink.api.Action;
import com.alipay.jarslink.api.Module;
import com.alipay.jarslink.api.ModuleConfig;
import com.alipay.jarslink.api.ModuleLoader;
import com.alipay.jarslink.api.ModuleManager;
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

    public static final String JARSLINK_MODULE_DEMO = "jarslink-module-demo-1.0.0.jar";

    @Autowired
    private ModuleManager moduleManager;

    @Autowired
    private ModuleLoader moduleLoader;

    @Test
    public void shouldLoadModule() {
        //1:加载模块
        Module module = loadModule();
        Assert.assertNotNull(module);
        Assert.assertNotNull(module.getCreation());
        Assert.assertNotNull(module.getChildClassLoader());
        //卸载模块
        module.destroy();
        Assert.assertNotNull(module.getChildClassLoader());
        removeModule(module);
    }

    private void removeModule(Module module) {
        moduleManager.remove(module.getName());
    }

    @Test
    public void shouldRegisterModule() throws MalformedURLException {
        //2:注册模块
        Module module = loadModule();
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

        removeModule(module);
    }

    @Test
    public void shouldDoOverrideAction() {
        //测试注解action被xml中的action覆盖
        ModuleConfig config = buildModuleConfig(true);
        config.addScanPackage("com.alipay.jarslink.main");
        config.addScanPackage("com.alipay.jarslink.demo");

        Module module = moduleLoader.load(config);
        Action<String, String> action = null;
        try {
            action = module.getAction("overrideXmlAction");
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
        Assert.assertNull(action);
        removeModule(module);
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
    }

    /**
     * 构建模块配置信息
     */
    public static ModuleConfig buildModuleConfig() {
        return buildModuleConfig(true);
    }

    public static ModuleConfig buildModuleConfig(boolean enabled) {
        URL demoModule;
        ModuleConfig moduleConfig = new ModuleConfig();
        //通过该方法构建的配置都是使用注解形式扫描bean的
        String scanBase = "com.alipay.jarslink.main";
        moduleConfig.addScanPackage(scanBase);
        moduleConfig.removeScanPackage(scanBase);
        Map<String, Object> properties = new HashMap();
        moduleConfig.withEnabled(enabled).withVersion("1.0.0.20170621").withOverridePackages(ImmutableList.of("com" +
                ".alipay.jarslink.demo")).withProperties(properties);
        demoModule = Thread.currentThread().getContextClassLoader().getResource(JARSLINK_MODULE_DEMO);

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

    private Module loadModule() {
        return moduleLoader.load(buildModuleConfig(true));
    }

    @Test
    public void shouldDoAction() {

        Module module = loadModule();
        String actionName = "helloworld";

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
        removeModule(module);
    }
}
