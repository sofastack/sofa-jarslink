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
package com.alipay.jarslink.api.springboot;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import com.alipay.jarslink.api.Module;
import com.alipay.jarslink.api.ModuleConfig;
import com.alipay.jarslink.api.ModuleLoader;
import com.alipay.jarslink.api.impl.ModuleClassLoader;
import com.alipay.jarslink.api.impl.SpringModule;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

/**
 * springboot模块服务实现
 *
 * @author suanping@msn.com
 * @version $Id: ModuleServiceImpl.java, v 0.1 2017年07月19日 9:28 PM tengfei.fangtf Exp $
 */
public class SbootModuleLoaderImpl implements ModuleLoader, ApplicationContextAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(SbootModuleLoaderImpl.class);

	/**
	 * 注入父applicationContext
	 */
	private ApplicationContext applicationContext;

	@Override
	public Module load(ModuleConfig moduleConfig) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Loading module: {}", moduleConfig);
		}
		List<String> tempFileJarURLs = moduleConfig.getModuleUrlPath();
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Local jars: {}", tempFileJarURLs);
		}

		ConfigurableApplicationContext moduleApplicationContext = loadModuleApplication(moduleConfig, tempFileJarURLs);

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Loading module  complete：{}", moduleConfig);
		}
		return new SpringModule(moduleConfig, moduleConfig.getVersion(), moduleConfig.getName(),
				moduleApplicationContext);
	}

	/**
	 * 根据本地临时文件Jar，初始化模块自己的ClassLoader，初始化Spring Application Context，同时要设置当前线程上下文的ClassLoader问模块的ClassLoader
	 *
	 * @param moduleConfig
	 * @param tempFileJarURLs
	 * @return
	 */
	private ConfigurableApplicationContext loadModuleApplication(ModuleConfig moduleConfig,
			List<String> tempFileJarURLs) {

		ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

		// 获取模块的ClassLoader
		ClassLoader moduleClassLoader = new ModuleClassLoader(moduleConfig.getModuleUrl(),
				applicationContext.getClassLoader(), getOverridePackages(moduleConfig));

		try {
			// 把当前线程的ClassLoader切换成模块的
			Thread.currentThread().setContextClassLoader(moduleClassLoader);

			return new SpringApplication(((SBootModuleConfig) moduleConfig).getAppMainClazz()).run();
		} catch (Throwable e) {
			CachedIntrospectionResults.clearClassLoader(moduleClassLoader);
			throw Throwables.propagate(e);
		} finally {
			// 还原当前线程的ClassLoader
			Thread.currentThread().setContextClassLoader(currentClassLoader);
		}
	}

	/**
	 * 去除list中的空白元素，string.startWith("")==true
	 *
	 * @param moduleConfig
	 * @return
	 */
	private List<String> getOverridePackages(ModuleConfig moduleConfig) {
		List<String> list = Lists.newArrayList();
		for (String s : moduleConfig.getOverridePackages()) {
			if (!StringUtils.isBlank(s)) {
				list.add(s);
			}
		}
		return list;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}