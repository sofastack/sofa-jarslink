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
 * 模块加载器
 *
 * @author tengfei.fangtf
 * @version $Id: ModuleLoader.java, v 0.1 2017年05月30日 2:55 PM tengfei.fangtf Exp $
 */
public interface ModuleLoader {

    /**
     * 根据配置加载一个模块，创建一个新的ClassLoadr加载jar里的class，初始化Spring ApplicationContext等
     *
     * @param moduleConfig 模块配置信息
     *
     * @return 加载成功的模块
     */
    Module load(ModuleConfig moduleConfig);

    /**
     * 卸载一个模块
     *
     * @param module
     */
    void unload(Module module);

}
