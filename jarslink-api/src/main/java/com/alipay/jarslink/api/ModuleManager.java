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

import java.util.List;
import java.util.Map;

/**
 * 模块管理者, 提供注册,移除和查找模块能力
 *
 * @author tengfei.fangtf
 * @version $Id: ModuleManager.java, v 0.1 2017年05月30日 2:55 PM tengfei.fangtf Exp $
 */
public interface ModuleManager {

    /**
     * 根据模块名查找Module
     * @param name
     * @return
     */
    Module find(String name);

    /**
     * 获取所有已加载的Module
     *
     * @return
     */
    List<Module> getModules();

    /**
     * 注册一个Module
     *
     * @param module 模块
     * @return 旧模块,如果没有旧模块则返回null
     */
    Module register(Module module);

    /**
     * 移除一个Module
     *
     * @param name 模块名
     * @return 被移除的模块
     */
    Module remove(String name);

    /**
     * 获取发布失败的模块异常信息
     *
     * @return key:模块名,value:错误信息
     */
    Map<String, String> getErrorModuleContext();

}
