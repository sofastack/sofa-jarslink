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
package com.alipay.jarslink.api;

/**
 * 模块服务类
 *
 * @author tengfei.fangtf
 * @version $Id: ModuleService.java, v 0.1 2017年07月19日 9:21 PM tengfei.fangtf Exp $
 */
public interface ModuleService {

    /**
     * 加载并注册模块,会移除和卸载旧的模块
     *
     * @param moduleConfig 模块配置信息
     * @return 注册成功的模块,如果模块不可用,则返回null
     */
    Module loadAndRegister(ModuleConfig moduleConfig);

}