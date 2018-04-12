package com.alipay.jarslink.demo.annotation;

import com.alipay.jarslink.api.Action;
import com.alipay.jarslink.support.annotation.ActionReference;
import org.springframework.stereotype.Component;

/**
 * action注入测试使用
 *
 * @author joe
 * @version 2018.04.04 14:42
 */
@Component
public class ActionProxy implements Action<String, String> {
    @ActionReference(moduleName = "demo-b", version = "1.0", name = "action-b")
    private Action<String, String> bAction;

    @Override
    public String execute(String actionRequest) {
        return bAction.execute(actionRequest);
    }

    @Override
    public String getActionName() {
        return "proxy-" + bAction.getActionName();
    }
}
