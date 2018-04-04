package com.alipay.jarslink.api;

/**
 * 在加载模块前调用
 *
 * @author joe
 * @version 2018.04.04 10:59
 */
public interface ModuleAware extends JarslinkAware {
    /**
     * 加载模块前调用
     *
     * @param moduleConfig 模块配置
     */
    void setModuleConfig(ModuleConfig moduleConfig);
}
