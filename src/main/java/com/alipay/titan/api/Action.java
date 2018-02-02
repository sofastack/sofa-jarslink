package com.alipay.titan.api;

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