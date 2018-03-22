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

import com.alipay.jarslink.api.ModuleConfig;

/**
 *
 * springboot 模块配置信息，
 *
 * @author suanping@msn.com
 * @version $Id: ModuleConfig.java, v 0.1 Mar 23, 2017 13:02:13 PM tengfei.fangtf Exp $
 */
public class SBootModuleConfig extends ModuleConfig {

	/** 应用入口class,类 full name */
	private String appMainClazz;

	/**
	 * @return the appMainClazz - {返回值描述信息}.
	 */
	public String getAppMainClazz() {
		return appMainClazz;
	}

	/**
	 * @param appMainClazz
	 *            to set - {参数含义描述}.
	 */
	public void setAppMainClazz(String appMainClazz) {
		this.appMainClazz = appMainClazz;
	}

}
