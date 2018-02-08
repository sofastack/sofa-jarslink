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

import com.alipay.jarslink.api.ModuleConfig;

import java.util.List;

/**
 * 模块刷新调度器
 *
 * @author tengfei.fangtf
 * @version $Id: AbstractModuleRefreshSchedulerImpl.java, v 0.1 2017年07月21日 2:50 PM tengfei.fangtf Exp $
 */
public class AbstractModuleRefreshSchedulerImpl extends AbstractModuleRefreshScheduler {

    private List<ModuleConfig> moduleConfigs;

    @Override
    public List<ModuleConfig> queryModuleConfigs() {
        return moduleConfigs;
    }

    public void setModuleConfigs(List<ModuleConfig> moduleConfigs) {
        this.moduleConfigs = moduleConfigs;
    }

}