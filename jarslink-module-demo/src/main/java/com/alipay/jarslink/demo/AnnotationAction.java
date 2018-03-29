package com.alipay.jarslink.demo;

import com.alipay.jarslink.api.Action;
import com.alipay.jarslink.api.ModuleConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author joe
 * @version 2018.03.29 09:33
 */
@Component("annotationAction")
public class AnnotationAction implements Action<ModuleConfig, ModuleConfig> {
    @Autowired
    private XmlAction xmlAction;

    @Override
    public ModuleConfig execute(ModuleConfig actionRequest) {
        return xmlAction.execute(actionRequest);
    }

    @Override
    public String getActionName() {
        return xmlAction.getActionName();
    }
}
