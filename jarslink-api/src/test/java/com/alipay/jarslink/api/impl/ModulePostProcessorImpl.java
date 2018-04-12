package com.alipay.jarslink.api.impl;

import com.alipay.jarslink.api.Module;
import com.alipay.jarslink.api.ModuleConfig;
import com.alipay.jarslink.api.ModulePostProcessor;
import org.junit.Assert;

/**
 * 测试用
 *
 * @author joe
 * @version 2018.04.04 11:44
 */
public class ModulePostProcessorImpl implements ModulePostProcessor {
    @Override
    public void setModule(Module module, ModuleConfig moduleConfig) {
        moduleConfig.withVersion("2.0");
        Assert.assertNotNull(module);
        Assert.assertNotNull(moduleConfig);
    }
}
