package com.alipay.titan.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 *
 * 模块配置信息
 *
 * @author tengfei.fangtf
 * @version $Id: ModuleConfig.java, v 0.1 Mar 23, 2017 13:02:13 PM tengfei.fangtf Exp $
 */
public class ModuleConfig {

    /**
     * 默认的ToStringStyle
     */
    public static final transient ToStringStyle DEFAULT_TO_STRING_STYLE = new DefaultToStringStyle();

    /**
     * 模块名,要求用英文命名,忽略大小写
     */
    private String name;

    /**
     * 模块描述
     */
    private String desc;

    /**
     * 是否启用模块,默认启用
     */
    private Boolean enabled = true;

    /**
     * 模块的版本，如1.0.0.20120609 版本变化会引起模块部署
     */
    private String version;

    /**
     * 模块需要的配置信息,类似于antx
     */
    private Map<String, Object> properties = Maps.newHashMap();

    /**
     * 模块自己覆盖的Class,不遵循双亲委派, 模块自己的类加载器加载这些包
     */
    private List<String> overridePackages = Lists.newArrayList();

    /**
     * JAR 包资源地址
     */
    private List<URL> moduleUrl = Lists.newArrayList();

    public List<URL> getModuleUrl() {
        return moduleUrl;
    }

    public List<String> getModuleUrlPath() {
        List<String> moduleUrls = Lists.newArrayList();
        for (URL url : moduleUrl) {
            moduleUrls.add(url.toString());
        }
        return moduleUrls;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setModuleUrl(List<URL> moduleUrl) {
        this.moduleUrl = moduleUrl;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ModuleConfig withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

    public ModuleConfig withVersion(String version) {
        setVersion(version);
        return this;
    }

    public ModuleConfig withProperties(Map<String, Object> properties) {
        setProperties(properties);
        return this;
    }

    public ModuleConfig withOverridePackages(List<String> overridePackages) {
        setOverridePackages(overridePackages);
        return this;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public List<String> getOverridePackages() {
        return overridePackages;
    }

    public void setOverridePackages(List<String> overridePackages) {
        this.overridePackages = overridePackages;
    }

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

}