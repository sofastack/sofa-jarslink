package com.alipay.jarslink.demo;

import com.alipay.jarslink.api.Action;

/**
 * 一个简单的执行Action实现
 * <pre>
 *    模拟用户需要执行某一样操作
 * </pre>
 * Created by liangyh on 2018/4/2.
 * Email:10856214@163.com
 */
public class ProcessAction implements Action<String, String> {

    @Override
    public String execute(String actionRequest) {
        System.out.println("成功执行===>"+actionRequest);
        return "SUCCESS";
    }

    @Override
    public String getActionName() {
        return "process";
    }
}
