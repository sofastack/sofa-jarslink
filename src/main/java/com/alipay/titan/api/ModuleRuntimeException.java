package com.alipay.titan.api;

/**
 * TITAN模块运行时异常
 *
 * @author tengfei.fangtf
 * @version $Id: ModuleRuntimeException.java, v 0.1 2017年05月30日 2:55 PM tengfei.fangtf Exp $
 */
public class ModuleRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ModuleRuntimeException(String message) {
        super(message);
    }

    public ModuleRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

}
