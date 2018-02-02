package com.alipay.titan.api.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Properties;

/**
 * 模块的Application Context，会注册一些模块使用的公用Bean
 *
 * @author tengfei.fangtf
 * @version $Id: ModuleApplicationContext.java, v 0.1 2017年05月30日 2:55 PM tengfei.fangtf Exp $
 */
public class ModuleApplicationContext extends ClassPathXmlApplicationContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleApplicationContext.class);

    /**
     * 模块属性信息配置
     */
    public static final String MODULE_PROPERTY_PLACEHOLDER_CONFIGURER = "modulePropertyPlaceholderConfigurer";

    /** 模块属性 */
    private Properties properties = new Properties();

    public ModuleApplicationContext(ApplicationContext parent) {
        super(parent);
    }

    @Override
    protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
        registerModulePropertiesPlaceHolderConfigurer(beanFactory);
        super.customizeBeanFactory(beanFactory);
    }

    /**
     * 将模块的属性配置设置到PropertyPlaceholderConfigurer里，并把PropertyPlaceholderConfigurer注册到上下文中
     *
     * @param beanFactory
     */
    private void registerModulePropertiesPlaceHolderConfigurer(DefaultListableBeanFactory beanFactory) {
        //通过GenericBeanDefinition创建bean
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        //设置bean资源属性的配置器
        beanDefinition.setBeanClass(PropertyPlaceholderConfigurer.class);
        //配置PropertyPlaceholderConfigurer的properties属性
        beanDefinition.getPropertyValues().add("properties", properties);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Register: {}", beanDefinition);
        }
        //注册bean
        beanFactory.registerBeanDefinition(MODULE_PROPERTY_PLACEHOLDER_CONFIGURER, beanDefinition);
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

}
