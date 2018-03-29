/*
 *
 *  * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package com.alipay.jarslink.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.google.common.base.Preconditions.checkNotNull;

/**
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
     * 模块名,建议用英文命名,忽略大小写
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
     * 如果bean重复或者模块中缺少bean是否允许使用当前容器中的bean替换模块中的bean，false表示不允许，默认不允许
     * <p>
     * <p>
     * <h3>使用场景：</h3>
     * <li>场景一：当前进程load一个模块A，模块A中有一个bean的定义与当前进程中的bean定义一致，此时如果设置该值为true的话将会
     * 使用当前进程的bean替换模块A中的bean。</li>
     * <p>
     * <li>场景二：当前进程load一个模块A，模块A中有一个依赖bean需要注入，但是模块A并没有提供该bean，而当前进程中有该bean，此时
     * 就可以使用当前进程的bean注入模块中使用。</li>
     * <p>
     * <h3>优势：</h3>
     * <li>开启该选项后一些公共bean可以由主进程加载而不用每个进程都加载，使用方式与引入servlet-api类似，只需将bean定义引入编
     * 译期即可（maven设置依赖scope为provide即可），节省内存，同时对于一些特殊bean可以防止模块重写而造成一些问题。</li>
     * <p>
     * <h3>风险：</h3>
     * <li>该功能启用未全面测试，仅进行简单的测试，可能会有一定未知风险，同时在某些场景可能会存在安全风险，请谨慎使用。</li>
     */
    private boolean enableOverrideModuleBean = false;

    /**
     * spring扫描注解的包，当该set不为空时启动包扫描，将自动扫描注解形式的bean
     */
    private Set<String> scanPackages = new CopyOnWriteArraySet<String>();

    /**
     * 模块的版本，如1.0.0.20120609 版本变化会触发模块重新部署
     */
    private String version;

    /**
     * 模块里的BEAN需要的配置信息,集成了SPING properties
     */
    private Map<String, Object> properties = Maps.newHashMap();

    /**
     * 模块指定需要覆盖的Class的包名,不遵循双亲委派, 模块的类加载器加载这些包
     */
    private List<String> overridePackages = Lists.newArrayList();

    /**
     * JAR 包资源地址,模块存放的地方
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

    /**
     * 获取当前系统是否允许使用当前进程的bean覆盖模块的bean
     *
     * @return 返回true表示允许
     */
    public boolean getEnableOverrideModuleBean() {
        return enableOverrideModuleBean;
    }

    /**
     * 设置是否允许使用当前进程的bean覆盖模块的bean
     *
     * @param enableOverrideModuleBean true表示允许，默认false，详细说明参照字段注释
     */
    public void setEnableOverrideModuleBean(boolean enableOverrideModuleBean) {
        this.enableOverrideModuleBean = enableOverrideModuleBean;
    }

    /**
     * 添加spring scan-base-package配置
     *
     * @param packageName 要添加的spring scan-base-package配置
     */
    public void addScanPackage(String packageName) {
        //不验证空字符串
        checkNotNull(packageName, "packageName must not be null");
        scanPackages.add(packageName);
    }

    /**
     * 移除指定现有的spring scan-base-package
     *
     * @param packageName 要移除的之前配置的spring scan-base-package
     */
    public void removeScanPackage(String packageName) {
        //不验证空字符串
        checkNotNull(packageName, "packageName must not be null");
        scanPackages.remove(packageName);
    }

    /**
     * 获取spring scan-base-package集合
     *
     * @return spring scan-base-package的集合
     */
    public Set<String> getScanPackages() {
        return scanPackages;
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