package com.alipay.jarslink.demo.annotation;

import com.alipay.jarslink.api.Action;
import org.springframework.stereotype.Component;

/**
 * @author joe
 * @version 2018.04.04 14:44
 */
@Component
public class AAction implements Action<String, String> {
    @Override
    public String execute(String actionRequest) {
        return "a-" + actionRequest;
    }

    @Override
    public String getActionName() {
        return "action-a";
    }
}
