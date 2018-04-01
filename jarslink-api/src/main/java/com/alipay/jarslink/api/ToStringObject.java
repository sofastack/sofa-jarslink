/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.jarslink.api;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 *
 * @author tengfei.fang
 * @version $Id: ToStringObject.java, v 0.1 2018年04月01日 12:39 AM tengfei.fang Exp $
 */
public class ToStringObject implements Serializable {

    /**
     * 默认的ToStringStyle
     */
    public static final transient ToStringStyle DEFAULT_TO_STRING_STYLE = new DefaultToStringStyle();

    /**
     * 默认的ToStringStyle
     */
    public static class DefaultToStringStyle extends ToStringStyle {

        private static final long serialVersionUID = 1L;

        public DefaultToStringStyle() {
            setUseShortClassName(true);
            setUseIdentityHashCode(false);
        }

        @Override
        public void append(StringBuffer buffer, String fieldName, Object value, Boolean fullDetail) {
            if (value != null) {
                super.append(buffer, fieldName, value, fullDetail);
            }
        }

        @Override
        public void append(StringBuffer buffer, String fieldName, Object[] array, Boolean fullDetail) {
            if (array != null && array.length > 0) {
                super.append(buffer, fieldName, array, fullDetail);
            }
        }
    }

    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, DEFAULT_TO_STRING_STYLE);
    }
}