package com.alipay.jarslink.demo.annotation;

import com.alipay.jarslink.api.Action;
import org.springframework.stereotype.Component;

/**
 * @author joe
 * @version 2018.04.04 14:39
 */
@Component
public class BAction implements Action<String ,String>{
    @Override
    public String execute(String actionRequest) {
        return "b-" + actionRequest;
    }

    @Override
    public String getActionName() {
        return "action-b";
    }
}
