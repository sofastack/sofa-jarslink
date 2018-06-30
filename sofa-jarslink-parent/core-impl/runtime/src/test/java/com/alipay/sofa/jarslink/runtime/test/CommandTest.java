/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.jarslink.runtime.test;

import com.alipay.sofa.ark.container.service.ArkServiceContainer;
import com.alipay.sofa.ark.spi.service.biz.BizFactoryService;
import com.alipay.sofa.ark.spi.service.biz.BizManagerService;
import com.alipay.sofa.jarslink.runtime.base.BaseTest;
import com.alipay.sofa.jarslink.runtime.command.DefaultCommandHandler;
import com.alipay.sofa.jarslink.runtime.service.BizServiceHolder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author qilong.zql
 * @since 0.1.0
 */
public class CommandTest extends BaseTest {

    private ArkServiceContainer   arkServiceContainer = new ArkServiceContainer();
    private BizFactoryService     bizFactoryService;
    private BizManagerService     bizManagerService;
    private String                filePath;

    private DefaultCommandHandler commandHandler      = new DefaultCommandHandler();

    @Before
    public void before() {
        arkServiceContainer.start();
        bizFactoryService = arkServiceContainer.getService(BizFactoryService.class);
        bizManagerService = arkServiceContainer.getService(BizManagerService.class);
        BizServiceHolder.setBizFactoryService(bizFactoryService);
        BizServiceHolder.setBizManagerService(bizManagerService);

        filePath = ClassLoader.getSystemResource("sample-biz.jar").toExternalForm();

    }

    @After
    public void after() {
        arkServiceContainer.stop();
    }

    @Test
    public void test() throws Exception {

        Assert.assertTrue(bizManagerService.getBiz("sample-biz").isEmpty());

        String response = commandHandler.handleCommand(String.format("install -b %s", filePath));
        Assert.assertTrue(response.contains("sample-biz:1.0.0"));

        while (bizManagerService.getActiveBiz("sample-biz") == null) {
            Thread.sleep(200);
        }

        response = commandHandler.handleCommand("check -b -n sample-biz -v 1.0.0");
        Assert.assertTrue(response.contains("bizState=\'activated\'"));

        response = commandHandler.handleCommand("switch -b -n sample-biz -v 1.0.0");
        Assert.assertTrue(response.contains("The specified biz's state must not be activated."));

        response = commandHandler.handleCommand("uninstall -b -v 1.0.0 -name sample-biz");
        Assert.assertTrue(response.contains("Uninstall biz:'sample-biz:1.0.0' success."));

        response = commandHandler.handleCommand("check -b -n sample-biz -v 1.0.0");
        Assert.assertTrue(response.contains("count=0"));

        Assert.assertTrue(bizManagerService.getBiz("sample-biz").isEmpty());
    }

}