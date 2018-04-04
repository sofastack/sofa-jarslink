package com.alipay.jarslink.support;

import com.alipay.jarslink.api.ModuleManager;
import com.alipay.jarslink.support.annotation.ActionReference;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author joe
 * @version 2018.04.03 16:45
 */
public class ActionFactoryBean implements FactoryBean, ApplicationContextAware, DisposableBean {
    private List<ActionBean> actionBeans = new CopyOnWriteArrayList<ActionBean>();
    private ModuleManager moduleManager;
    private ActionReference reference;
    private ActionBean ref;

    public ActionFactoryBean(ModuleManager moduleManager, ActionReference reference) {
        this.moduleManager = moduleManager;
        this.reference = reference;
        this.ref = new ActionBean(moduleManager, reference);
    }

    @Override
    public void destroy() throws Exception {
        ref = null;
    }

    @Override
    public Object getObject() throws Exception {
        return ref;
    }

    @Override
    public Class<?> getObjectType() {
        return ActionBean.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
