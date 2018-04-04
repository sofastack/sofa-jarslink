package com.alipay.jarslink.demo.annotation;

import com.alipay.jarslink.api.Action;
import com.alipay.jarslink.support.annotation.ActionReference;
import org.springframework.stereotype.Component;

/**
 * 注入其他模块的action
 *
 * @author joe
 * @version 2018.04.04 14:40
 */
@Component
public class ActionProxy implements Action<String , String>{
    @ActionReference(moduleName = "demo-a" , version = "1.0" , name = "action-a")
    private Action<String , String> aAction;

    @Override
    public String execute(String actionRequest) {
        return aAction.execute(actionRequest);
    }

    @Override
    public String getActionName() {
        return "proxy-" + aAction.getActionName();
    }
}
