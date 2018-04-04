package com.alipay.jarslink.api;

import org.springframework.context.ConfigurableApplicationContext;

/**
 * 加载模块时在创建完模块的spring-context并且刷新spring-context后调用，用于做一些拦截处理
 *
 * @author joe
 * @version 2018.04.04 10:53
 */
public interface ApplicationContextPostProcessor extends JarslinkPostProcessor {
    /**
     * 加载模块时在创建完模块的spring-context并且刷新spring-context后调用
     *
     * @param context      当前加载的模块对应的configurableApplicationContext
     * @param moduleConfig 当前加载的模块对应的配置
     */
    void setConfigurableApplicationContext(ConfigurableApplicationContext context, ModuleConfig moduleConfig);
}
