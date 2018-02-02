package com.alipay.titan.api;

import java.util.List;
import java.util.Map;

/**
 * 模块管理者, 提供注册,移除和查找模块能力
 *
 * @author tengfei.fangtf
 * @version $Id: ModuleManager.java, v 0.1 2017年05月30日 2:55 PM tengfei.fangtf Exp $
 */
public interface ModuleManager {

    /**
     * 根据模块名查找Module
     * @param name
     * @return
     */
    Module find(String name);

    /**
     * 获取所有已加载的Module
     *
     * @return
     */
    List<Module> getModules();

    /**
     * 注册一个Module
     *
     * @param module 模块
     * @return 旧模块,如果没有旧模块则返回null
     */
    Module register(Module module);

    /**
     * 移除一个Module
     *
     * @param name 模块名
     * @return 被移除的模块
     */
    Module remove(String name);

    /**
     * 获取发布失败的模块异常信息
     *
     * @return key:模块名,value:错误信息
     */
    Map<String, String> getErrorModuleContext();

}
