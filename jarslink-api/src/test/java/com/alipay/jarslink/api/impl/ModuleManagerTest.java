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
import com.alipay.jarslink.api.ModuleLoader;
import com.alipay.jarslink.api.ModuleManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.MalformedURLException;

import static com.alipay.jarslink.api.impl.ModuleLoaderImplTest.buildModuleConfig;

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
    public void shouldRegisterModule() throws MalformedURLException {
        //注册一个模块
        Module module = loadModule("demo", "1.0");
        Module removedModule = moduleManager.register(module);
        Assert.assertNull(removedModule);

        ////再注册同一个模块不同版本,会卸载旧的模块
        Module newModule = loadModule("demo", "2.0");
        removedModule = moduleManager.register(newModule);
        Assert.assertEquals(1, moduleManager.getModules().size());

        //查找模块
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
    public void shouldNotRegisterSameVersionModule() {
        //注册一个模块
        Module module = loadModule();
        Module removedModule = moduleManager.register(module);
        Assert.assertNull(removedModule);
        Assert.assertEquals(1, moduleManager.getModules().size());

        //再注册同一个模块,不会注册成功,返回空
        Module register = moduleManager.register(module);
        Assert.assertNull(register);
        Assert.assertEquals(1, moduleManager.getModules().size());
    }

    private Module loadModule() {
        return moduleLoader.load(buildModuleConfig(true));
    }

    private Module loadModule(String name) {
        return moduleLoader.load(buildModuleConfig(name, "1.0.0.20170621", true));
    }

    private void removeModule(Module module) {
        moduleManager.remove(module.getName());
    }

    private Module loadModule(String name, String version) {
        return moduleLoader.load(buildModuleConfig(name, version, true));
    }

}
