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
import com.alipay.jarslink.api.ModuleConfig;
import com.alipay.jarslink.api.ModuleManager;
import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

import static com.alipay.jarslink.api.impl.ModuleLoaderImplTest.buildModuleConfig;

/**
 * JarsLink API入口,使用TITAN API必须继承AbstractModuleRefreshScheduler然后提供模块信息
 *
 * @author tengfei.fangtf
 * @version $Id: AbstractModuleRefreshSchedulerTest.java, v 0.1 2017年06月26日 10:01 AM tengfei.fangtf Exp $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/jarslink.xml",
        "classpath*:META-INF/spring/jarslink-schedule.xml"})
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
        abstractModuleRefreshSchedulerImpl.setModuleConfigs(ImmutableList.of(buildModuleConfig()));
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
        abstractModuleRefreshSchedulerImpl.setModuleConfigs(ImmutableList.of(buildModuleConfig
                (true)));
        Assert.assertEquals(1, abstractModuleRefreshSchedulerImpl.queryModuleConfigs().size());
        abstractModuleRefreshSchedulerImpl.run();
        Module demo = moduleManager.find("demo");
        Assert.assertNotNull(demo.getAction("helloworld"));

        //修改模块
        ModuleConfig moduleConfig = buildModuleConfig(true);
        moduleConfig.setVersion("1.1");
        abstractModuleRefreshSchedulerImpl.setModuleConfigs(ImmutableList.of(moduleConfig));
        abstractModuleRefreshSchedulerImpl.run();

        //此处由于此前已经存在该模块，所以必须要激活才能使用
        moduleManager.activeVersion("demo", moduleConfig.getVersion());
        demo = moduleManager.find(moduleConfig.getName());
        //上述部分可以替换为下面的写法
        //        demo = moduleManager.find(moduleConfig.getName(), moduleConfig.getVersion());

        Assert.assertEquals(moduleConfig.getVersion(), demo.getVersion());

    }

}