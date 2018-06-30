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
package com.alipay.sofa.jarslink.runtime.service;

import com.alipay.sofa.ark.common.util.AssertUtils;
import com.alipay.sofa.ark.spi.service.biz.BizFactoryService;
import com.alipay.sofa.ark.spi.service.biz.BizManagerService;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author qilong.zql
 * @since 0.1.0
 */
public class BizServiceHolder {

    private static BizManagerService bizManagerService;

    private static BizFactoryService bizFactoryService;

    private static AtomicBoolean     setBizManager = new AtomicBoolean(false);
    private static AtomicBoolean     setBizFactory = new AtomicBoolean(false);

    public static void setBizManagerService(BizManagerService bizManagerService) {
        AssertUtils.assertNotNull(bizManagerService, "BizManagerService must not be null.");
        if (setBizManager.compareAndSet(false, true)) {
            BizServiceHolder.bizManagerService = bizManagerService;
        }
    }

    public static void setBizFactoryService(BizFactoryService bizFactoryService) {
        AssertUtils.assertNotNull(bizFactoryService, "BizFactoryService must not be null");
        if (setBizFactory.compareAndSet(false, true)) {
            BizServiceHolder.bizFactoryService = bizFactoryService;
        }
    }

    public static BizManagerService getBizManagerService() {
        return BizServiceHolder.bizManagerService;
    }

    public static BizFactoryService getBizFactoryService() {
        return BizServiceHolder.bizFactoryService;
    }

}