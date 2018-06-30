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
package com.alipay.sofa.jarslink.integration.deployer;

import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.ark.spi.service.ArkInject;
import com.alipay.sofa.ark.spi.service.biz.BizDeployer;
import com.alipay.sofa.ark.spi.service.biz.BizFactoryService;
import com.alipay.sofa.ark.spi.service.biz.BizManagerService;
import com.alipay.sofa.jarslink.common.log.JSKLogger;
import com.alipay.sofa.jarslink.common.log.JSKLoggerFactory;
import com.alipay.sofa.jarslink.runtime.service.BizServiceHolder;
import com.alipay.sofa.jarslink.spi.constant.Constants;

/**
 * A deployer to deploy ark biz
 * {@link BizDeployer}
 *
 * @author qilong.zql
 * @since 0.1.0
 */
public class JarslinkDeployer implements BizDeployer {

    private static final JSKLogger LOGGER = JSKLoggerFactory.getDefaultLogger();

    @ArkInject
    private BizManagerService      bizManagerService;

    @ArkInject
    private BizFactoryService      bizFactoryService;

    private String[]               arguments;

    @Override
    public void init(String[] args) {
        this.arguments = args;
        BizServiceHolder.setBizManagerService(bizManagerService);
        BizServiceHolder.setBizFactoryService(bizFactoryService);
    }

    @Override
    public void deploy() {
        for (Biz biz : bizManagerService.getBizInOrder()) {
            try {
                LOGGER.info(String.format("Begin to start biz: %s", biz.getBizName()));
                biz.start(arguments);
                LOGGER.info(String.format("Finish to start biz: %s", biz.getBizName()));
            } catch (Throwable e) {
                LOGGER.error(String.format("Start biz: %s meet error", biz.getBizName()), e);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void unDeploy() {
        for (Biz biz : bizManagerService.getBizInOrder()) {
            try {
                LOGGER.info(String.format("Begin to stop biz: %s", biz.getBizName()));
                biz.stop();
                LOGGER.info(String.format("Finish to stop biz: %s", biz.getBizName()));
            } catch (Throwable e) {
                LOGGER.error(String.format("Stop biz: %s meet error", biz.getBizName()), e);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String getDesc() {
        return String.format("{name=\'%s\', provider=\'%s\'}", Constants.JARSLINK_DEPLOYER,
            Constants.JARSLINK_IDENTITY);
    }
}