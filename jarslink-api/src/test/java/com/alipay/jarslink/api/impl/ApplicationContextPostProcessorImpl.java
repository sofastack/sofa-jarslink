package com.alipay.jarslink.api.impl;

import com.alipay.jarslink.api.ApplicationContextPostProcessor;
import com.alipay.jarslink.api.ModuleConfig;
import org.junit.Assert;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 测试用
 *
 * @author joe
 * @version 2018.04.04 11:44
 */
public class ApplicationContextPostProcessorImpl implements ApplicationContextPostProcessor {
    @Override
    public void setConfigurableApplicationContext(ConfigurableApplicationContext context, ModuleConfig moduleConfig) {
        moduleConfig.withVersion("2.0");
        Assert.assertNotNull(context);
        Assert.assertNotNull(moduleConfig);
    }
}
