package com.alipay.jarslink.demo;

import com.alipay.jarslink.api.Action;
import com.alipay.jarslink.api.ModuleConfig;

/**
 * 一个简单的Action实现
 */
public class HelloWorldAction implements Action<ModuleConfig, ModuleConfig> {
    private AnnotationAction annotationAction;

    @Override
    public ModuleConfig execute(ModuleConfig actionRequest) {
        return annotationAction.execute(actionRequest);
    }

    @Override
    public String getActionName() {
        return annotationAction.getActionName();
    }

    public AnnotationAction getAnnotationAction() {
        return annotationAction;
    }

    public void setAnnotationAction(AnnotationAction annotationAction) {
        this.annotationAction = annotationAction;
    }
}
