package com.alipay.jarslink.api.impl;

import com.alipay.jarslink.api.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * module工具类
 *
 * @author joe
 * @version 2018.03.24 23:16
 */
public class ModuleTools {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleTools.class);

    static void destroyQuietly(Module module) {
        if (module != null) {
            try {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Destroy module: {}", module.getName());
                }
                module.destroy();
            } catch (Exception e) {
                LOGGER.error("Failed to destroy module " + module, e);
            }
        }
    }
}
