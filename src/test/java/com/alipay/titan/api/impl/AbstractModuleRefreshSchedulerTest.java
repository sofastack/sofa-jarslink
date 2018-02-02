/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2017 All Rights Reserved.
 */

package com.alipay.titan.api.impl;

import com.alipay.titan.api.Module;
import com.alipay.titan.api.ModuleConfig;
import com.alipay.titan.api.ModuleManager;
import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

/**
 * TITAN API入口,使用TITAN API必须继承AbstractModuleRefreshScheduler然后提供模块信息
 *
 * @author tengfei.fangtf
 * @version $Id: AbstractModuleRefreshSchedulerTest.java, v 0.1 2017年06月26日 10:01 AM tengfei.fangtf Exp $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/titan.xml", "classpath*:META-INF/spring/titan-schedule.xml"})
public class AbstractModuleRefreshSchedulerTest {

    @Autowired
    private AbstractModuleRefreshSchedulerImpl abstractModuleRefreshSchedulerImpl;

    @Autowired
    private ModuleManager moduleManager;

    @Before
    public void init() {
        //不进行自动触发,改成手动触发
        abstractModuleRefreshSchedulerImpl.setRefreshDelay(600);
        abstractModuleRefreshSchedulerImpl.setInitialDelay(600);
    }

    @Test
    public void shouldAddModule() {
        //装载模块
        abstractModuleRefreshSchedulerImpl.setModuleConfigs(ImmutableList.of(ModuleManagerTest.buildModuleConfig()));
        Assert.assertEquals(1, abstractModuleRefreshSchedulerImpl.queryModuleConfigs().size());
        abstractModuleRefreshSchedulerImpl.run();
        Module demo = moduleManager.find("demo");
        Assert.assertNotNull(demo.getAction("helloworld"));

        ////修改模块
        //ModuleConfig moduleConfig = ModuleManagerTest.buildModuleConfig(false);
        //moduleConfig.setVersion("1.1");
        //abstractModuleRefreshSchedulerImpl.queryModuleConfigs().add(moduleConfig);
        //abstractModuleRefreshSchedulerImpl.run();

        //卸载模块
        abstractModuleRefreshSchedulerImpl.setModuleConfigs(new ArrayList<ModuleConfig>());
        Assert.assertEquals(0, abstractModuleRefreshSchedulerImpl.queryModuleConfigs().size());
        abstractModuleRefreshSchedulerImpl.run();
        Assert.assertNull(moduleManager.find("demo"));

    }

    @Test
    public void shouldUpdateModule() {
        //装载模块
        abstractModuleRefreshSchedulerImpl.setModuleConfigs(ImmutableList.of(ModuleManagerTest.buildModuleConfig()));
        Assert.assertEquals(1, abstractModuleRefreshSchedulerImpl.queryModuleConfigs().size());
        abstractModuleRefreshSchedulerImpl.run();
        Module demo = moduleManager.find("demo");
        Assert.assertNotNull(demo.getAction("helloworld"));

        //修改模块
        ModuleConfig moduleConfig = ModuleManagerTest.buildModuleConfig(true);
        moduleConfig.setVersion("1.1");
        abstractModuleRefreshSchedulerImpl.setModuleConfigs(ImmutableList.of(moduleConfig));
        abstractModuleRefreshSchedulerImpl.run();
        demo = moduleManager.find(moduleConfig.getName());
        Assert.assertEquals(moduleConfig.getVersion(), demo.getVersion());

    }

}