package com.alipay.jarslink.demo;

import com.alipay.jarslink.api.Action;
import org.springframework.stereotype.Component;

/**
 * @author joe
 * @version 2018.04.01 00:03
 */
@Component("xmlAction")
public class OverrideXmlAction implements Action<String, String> {
    @Override
    public String execute(String actionRequest) {
        return "overrideXml:" + actionRequest;
    }

    @Override
    public String getActionName() {
        return "overrideXmlAction";
    }
}
