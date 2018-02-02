/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2017 All Rights Reserved.
 */
package com.alipay.titan.api;

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
     * @param moduleConfig
     * @return 注册成功的模块,如果模块不可用,则返回null
     */
    Module loadAndRegister(ModuleConfig moduleConfig);

}