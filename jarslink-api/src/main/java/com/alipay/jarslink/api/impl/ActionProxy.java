package com.alipay.jarslink.api.impl;

import com.alipay.jarslink.api.Action;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * action代理
 *
 * @author joe
 * @version 2018.04.16 22:33
 */
class ActionProxy<R, T> implements Action<R, T> {
    private Action<R, T> action;

    /**
     * 默认构造器
     *
     * @param action 要代理的Action
     */
    public ActionProxy(Action<R, T> action) {
        this.action = action;
    }

    /**
     * 关闭action，便于回收
     */
    public void destroy() {
        action = null;
    }

    @Override
    public T execute(R actionRequest) {
        checkNotNull(action, "current module was closed");
        return action.execute(actionRequest);
    }

    @Override
    public String getActionName() {
        checkNotNull(action, "current module was closed");
        return action.getActionName();
    }
}
