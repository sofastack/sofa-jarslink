/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2017 All Rights Reserved.
 */
package com.alipay.titan.api.impl;

import com.alipay.titan.api.Module;
import com.alipay.titan.api.ModuleConfig;
import com.alipay.titan.api.ModuleService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author tengfei.fangtf
 * @version $Id: ModuleServiceTest.java, v 0.1 2017年07月21日 10:33 AM tengfei.fangtf Exp $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/titan.xml"})
public class ModuleServiceTest{

    @Autowired
    private ModuleService moduleService;

    @Test
    public void shouldLoadAndRegister() {
        //先加载并注册模块
        ModuleConfig moduleConfig = ModuleManagerTest.buildModuleConfig();
        Module module = moduleService.loadAndRegister(moduleConfig);
        Assert.assertNotNull(module);
        Assert.assertNotNull(module.getCreation());
        Assert.assertNotNull(module.getChildClassLoader());
        Assert.assertEquals(moduleConfig.getName(),module.getName());
        Assert.assertEquals(moduleConfig.getVersion(),module.getVersion());

        //再禁用模块
        module = moduleService.loadAndRegister(ModuleManagerTest.buildModuleConfig(false));
        Assert.assertNull(module);

    }

}