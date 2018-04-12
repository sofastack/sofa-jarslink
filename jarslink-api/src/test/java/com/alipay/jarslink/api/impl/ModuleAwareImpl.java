package com.alipay.jarslink.api.impl;

import com.alipay.jarslink.api.ModuleAware;
import com.alipay.jarslink.api.ModuleConfig;
import org.junit.Assert;

/**
 * 测试用
 *
 * @author joe
 * @version 2018.04.04 11:47
 */
public class ModuleAwareImpl implements ModuleAware {
    @Override
    public void setModuleConfig(ModuleConfig moduleConfig) {
        moduleConfig.withVersion("2.0");
        Assert.assertNotNull(moduleConfig);
    }
}
