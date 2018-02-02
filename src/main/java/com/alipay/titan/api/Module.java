package com.alipay.titan.api;

import java.util.Date;
import java.util.Map;

/**
 * TITAN模块
 *
 * @author tengfei.fangtf
 * @version $Id: Module.java, v 0.1 2017年05月30日 2:55 PM tengfei.fangtf Exp $
 */
public interface Module {

    /**
     * 查找处理请求的Action
     *
     * @param actionName
     * @return
     */
    <R, T> Action<R, T> getAction(String actionName);

    /**
     * 获取全部的Action
     *
     * @return action名称和Action对象
     */
    Map<String, Action> getActions();

    /**
     * 查找处理请求的Action
     *
     * @param actionName
     * @param actionRequest
     * @return
     */
    <R, T> T doAction(String actionName, R actionRequest);

    /**
     * 模块名
     *
     * @return
     */
    String getName();

    /**
     * 模块版本号
     *
     * @return
     */
    String getVersion();

    /**
     * 模块的创建时间
     *
     * @return
     */
    Date getCreation();

    /**
     * 销毁
     */
    void destroy();

    /**
     * 获取spring上下
     *
     * @return
     */
    ClassLoader getChildClassLoader();


    /**
     * 获取模块配置
     *
     * @return
     */
    ModuleConfig getModuleConfig();

}
