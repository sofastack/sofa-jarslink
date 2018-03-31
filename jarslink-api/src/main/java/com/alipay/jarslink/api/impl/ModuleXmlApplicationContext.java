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

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Properties;

/**
 * 模块的Application Context，会注册一些模块使用的公用Bean
 *
 * @author tengfei.fangtf
 * @version $Id: ModuleXmlApplicationContext.java, v 0.1 2017年05月30日 2:55 PM tengfei.fangtf Exp $
 */
public class ModuleXmlApplicationContext extends ClassPathXmlApplicationContext {

    /**
     * 模块属性
     */
    private Properties properties = new Properties();

    @Override
    protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
        ModuleUtil.registerModulePropertiesPlaceHolderConfigurer(beanFactory, properties);
        super.customizeBeanFactory(beanFactory);
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

}
