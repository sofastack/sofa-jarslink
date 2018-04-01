package com.alipay.jarslink.demo;

import com.alipay.jarslink.api.Action;
import org.springframework.stereotype.Component;

/**
 * 此Action与{@link com.alipay.jarslink.demo.XmlAction}name相同，{@link com.alipay.jarslink.demo.XmlAction}定义在xml
 * 中，此Action将被覆盖，jarslink查找不到该action
 *
 * @see com.alipay.jarslink.demo.XmlAction
 *
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
