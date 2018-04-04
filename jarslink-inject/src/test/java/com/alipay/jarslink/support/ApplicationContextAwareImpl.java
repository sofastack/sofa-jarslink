package com.alipay.jarslink.support;

import com.alipay.jarslink.api.ApplicationContextAware;
import com.alipay.jarslink.api.ModuleConfig;
import com.alipay.jarslink.api.ModuleManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author joe
 * @version 2018.04.04 14:54
 */
public class ApplicationContextAwareImpl implements ApplicationContextAware {
    private final ReferenceAnnotationBeanPostProcessor postProcessor;

    public ApplicationContextAwareImpl(ModuleManager moduleManager) {
        this.postProcessor = new ReferenceAnnotationBeanPostProcessor(moduleManager);
    }

    @Override
    public void setConfigurableApplicationContext(ConfigurableApplicationContext context, ModuleConfig moduleConfig) {
        context.addBeanFactoryPostProcessor(new BeanFactoryPostProcessor() {
            @Override
            public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
                beanFactory.addBeanPostProcessor(postProcessor);
            }
        });
    }
}
