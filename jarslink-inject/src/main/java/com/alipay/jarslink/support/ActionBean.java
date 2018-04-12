package com.alipay.jarslink.support;

import com.alipay.jarslink.api.Action;
import com.alipay.jarslink.api.Module;
import com.alipay.jarslink.api.ModuleManager;
import com.alipay.jarslink.support.annotation.ActionReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * action bean
 *
 * @author joe
 * @version 2018.04.03 17:17
 */
public class ActionBean implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionBean.class);
    private final ModuleManager moduleManager;
    private final ActionReference reference;
    private String version;
    private String moduleName;
    private String actionName;

    public ActionBean(ModuleManager moduleManager, ActionReference reference) {
        this.moduleManager = moduleManager;
        this.reference = reference;
        init();
    }

    private void init() {
        version = reference.version();
        moduleName = reference.moduleName();
        actionName = reference.name();
        Utils.checkEmpty(version, "module version must not be null or empty");
        Utils.checkEmpty(moduleName, "module version must not be null or empty");
        Utils.checkEmpty(actionName, "module version must not be null or empty");
    }

    @Override
    public Object execute(Object t) {
        Module module = moduleManager.find(moduleName, version);
        if (module == null) {
            LOGGER.error("module[" + moduleName + ":" + version + "] not exist");
            throw new IllegalStateException("module[" + moduleName + ":" + version + "] not exist");
        }
        Action action = null;
        try {
            action = module.getAction(actionName);
        } catch (NullPointerException e) {
            if (action == null) {
                LOGGER.error("action[" + moduleName + ":" + version + ":" + actionName + "] not exist");
                throw new IllegalStateException("action[" + moduleName + ":" + version + ":" + actionName + "] not " +
                        "exist");
            }
        }

        return action.execute(t);
    }

    @Override
    public String getActionName() {
        return actionName;
    }
}
