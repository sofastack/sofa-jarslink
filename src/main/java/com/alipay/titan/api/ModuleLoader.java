package com.alipay.titan.api;

/**
 * 模块加载器
 *
 * @author tengfei.fangtf
 * @version $Id: ModuleLoader.java, v 0.1 2017年05月30日 2:55 PM tengfei.fangtf Exp $
 */
public interface ModuleLoader {

    /**
     * 根据配置加载一个模块，创建一个新的ClassLoadr加载jar里的class，初始化Spring ApplicationContext等
     *
     * @param moduleConfig 模块配置信息
     *
     * @return 加载成功的模块
     */
    Module load(ModuleConfig moduleConfig);

}
