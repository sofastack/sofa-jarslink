/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2017 All Rights Reserved.
 */
package com.alipay.titan.api.impl;

import com.alipay.titan.api.ModuleConfig;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
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