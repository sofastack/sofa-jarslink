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
import com.alipay.jarslink.api.Action;
import com.alipay.jarslink.api.ModuleConfig;
import com.alipay.jarslink.api.ModuleLoader;
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
        Module module = loadModule(false);
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
        Module module = loadModule(false);
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
    public void shouldDoActionByXml() {
        shouldDoAction(false);
    }

    @Test
    public void shouldDoActionByAnnotation() {
        shouldDoAction(true);
    }

    /**
     * 构建模块配置信息
     */
    public static ModuleConfig buildModuleConfig(boolean annotation) {
        return buildModuleConfig(true, annotation);
    }

    public static ModuleConfig buildModuleConfig(boolean enabled, boolean annotation) {
        URL demoModule;
        ModuleConfig moduleConfig = new ModuleConfig();
        if (annotation) {
            //增加spring扫描配置，自动发现注解形式的bean
            moduleConfig.addScanBase("com.alipay.jarslink.demo");
            demoModule = Thread.currentThread().getContextClassLoader().getResource
                    ("jarslink-module-demo-annotation-1.0.0.jar");
        } else {
            demoModule = Thread.currentThread().getContextClassLoader().getResource
                    ("jarslink-module-demo-xml-1.0.0.jar");
        }

        moduleConfig.setName("demo");
        moduleConfig.setEnabled(enabled);
        moduleConfig.setOverridePackages(ImmutableList.of("com.alipay.jarslink.demo"));
        moduleConfig.setVersion("1.0.0.20170621");
        Map<String, Object> properties = new HashMap();
        properties.put("url", "127.0.0.1");
        moduleConfig.setProperties(properties);
        moduleConfig.setModuleUrl(ImmutableList.of(demoModule));
        return moduleConfig;
    }

    private Module loadModule(boolean annotation) {
        return moduleLoader.load(buildModuleConfig(annotation));
    }

    private void shouldDoAction(boolean annotation) {
        Module findModule = loadModule(annotation);
        Module removedModule = moduleManager.register(findModule);
        //4.1:查找和执行Action

        String actionName = "helloworld";
        ModuleConfig moduleConfig = new ModuleConfig();
        moduleConfig.setName("h");
        moduleConfig.setEnabled(true);
        ModuleConfig result = findModule.doAction(actionName, moduleConfig);
        Assert.assertEquals(1, findModule.getActions().size());
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getName(), moduleConfig.getName());
        Assert.assertEquals(result.getEnabled(), moduleConfig.getEnabled());

        //4.2:查找和执行Action
        Action<ModuleConfig, ModuleConfig> action = findModule.getAction(actionName);
        Assert.assertNotNull(action);
        result = action.execute(moduleConfig);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getName(), moduleConfig.getName());
        Assert.assertEquals(result.getEnabled(), moduleConfig.getEnabled());

        //卸载模块
        moduleManager.remove(findModule.getName());
    }
}