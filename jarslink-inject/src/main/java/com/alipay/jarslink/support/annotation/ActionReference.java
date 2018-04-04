package com.alipay.jarslink.support.annotation;

import java.lang.annotation.*;

/**
 * 用于在模块中注入Action
 *
 * @author joe
 * @version 2018.04.03 16:37
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface ActionReference {
    /**
     * module name
     *
     * @return module name
     */
    String moduleName();

    /**
     * module version
     *
     * @return module version
     */
    String version();

    /**
     * action name
     *
     * @return action name
     */
    String name();
}
