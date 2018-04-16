package com.alipay.jarslink.error;

import com.alipay.jarslink.api.Action;
import org.springframework.stereotype.Component;

/**
 * @author joe
 * @version 2018.04.16 23:27
 */
@Component
public class NullNameAction implements Action<String, String> {
    @Override
    public String execute(String actionRequest) {
        return null;
    }

    @Override
    public String getActionName() {
        return null;
    }
}
