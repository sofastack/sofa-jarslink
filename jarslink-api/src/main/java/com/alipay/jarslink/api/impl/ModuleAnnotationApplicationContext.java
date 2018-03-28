package com.alipay.jarslink.api.impl;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Properties;

/**
 * @author joe
 * @version 2018.03.28 23:06
 */
public class ModuleAnnotationApplicationContext extends AnnotationConfigApplicationContext {

    public ModuleAnnotationApplicationContext(Properties properties) {
        properties = properties == null ? new Properties() : properties;
        ModuleUtil.registerModulePropertiesPlaceHolderConfigurer(this, properties);
    }
}
