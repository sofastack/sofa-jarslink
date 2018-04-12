package com.alipay.jarslink.support;

/**
 * 工具类
 *
 * @author joe
 * @version 2018.04.03 17:27
 */
public class Utils {
    /**
     * 检查字符串非空，如果字符串是空则抛出异常
     *
     * @param str      字符串
     * @param errorMsg 错误消息
     */
    public static void checkEmpty(String str, String errorMsg) {
        if (str == null || str.trim().equals("")) {
            throw new IllegalArgumentException(errorMsg);
        }
    }
}
