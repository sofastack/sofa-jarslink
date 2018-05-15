package com.alipay.jarslink.demo;

import com.alipay.jarslink.api.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * annotation bean  由注解扫描发现注册，同时该action注入了{@link com.alipay.jarslink.demo.XmlAction}，XmlAction定义在xml中
 *
 * @author joe
 * @version 2018.03.31 23:10
 */
@Component
public class AnnotationAction implements Action<String, String> {
    @Autowired
    private XmlAction xmlAction;

    @Override
    public String execute(String actionRequest) {
        return xmlAction.execute(actionRequest);
    }

    @Override
    public String getActionName() {
        return "annotationAction";
    }
}
