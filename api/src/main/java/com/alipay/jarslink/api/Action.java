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
 * 模块的执行者
 *
 * @author tengfei.fangtf
 * @version $Id: Action.java, v 0.1 2017年05月30日 2:55 PM tengfei.fangtf Exp $
 */
public interface Action<R, T> {

    /**
     * 处理请求
     *
     * @param actionRequest 请求对象
     *
     * @return 响应对象
     */
    T execute(R actionRequest);

    /**
     * 获取Action名称
     *
     * @return Action名称, 忽略大小写
     */
    String getActionName();

}