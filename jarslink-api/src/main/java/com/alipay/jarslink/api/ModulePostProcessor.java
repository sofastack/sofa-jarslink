package com.alipay.jarslink.api;

/**
 * module创建完后会调用该回调
 *
 * @author joe
 * @version 2018.04.04 11:01
 */
public interface ModulePostProcessor extends JarslinkPostProcessor {
    /**
     * module创建完后调用
     *
     * @param module       模块
     * @param moduleConfig 模块对应的配置
     */
    void setModule(Module module, ModuleConfig moduleConfig);
}
