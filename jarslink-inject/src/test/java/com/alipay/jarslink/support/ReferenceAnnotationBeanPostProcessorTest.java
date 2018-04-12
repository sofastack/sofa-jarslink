package com.alipay.jarslink.support;

import com.alipay.jarslink.api.*;
import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URL;

/**
 * @author joe
 * @version 2018.04.04 14:22
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/jarslink.xml"})
public class ReferenceAnnotationBeanPostProcessorTest {
    public static final String JARSLINK_MODULE_DEMO_A = "jarslink-module-a-1.0.0.jar";
    public static final String JARSLINK_MODULE_DEMO_B = "jarslink-module-b-1.0.0.jar";
    @Autowired
    private ModuleManager moduleManager;
    @Autowired
    private ModuleLoader moduleLoader;

    @Before
    public void init() {
        ApplicationContextAware aware = new ApplicationContextAwareImpl(moduleManager);
        moduleLoader.registerAware(aware);
    }

    @Test
    public void shouldInject() {
        ModuleConfig configA = buildModuleConfig("demo-a", "1.0", true, JARSLINK_MODULE_DEMO_A);
        ModuleConfig configB = buildModuleConfig("demo-b", "1.0", true, JARSLINK_MODULE_DEMO_B);
        Module moduleA = moduleLoader.load(configA);
        Module moduleB = moduleLoader.load(configB);
        moduleManager.register(moduleA);
        moduleManager.register(moduleB);
        Assert.assertNotNull(moduleA);
        Assert.assertNotNull(moduleB);
        String result = moduleA.doAction("action-a", "request");
        Assert.assertEquals("a-request", result);
        result = moduleB.doAction("action-b", "request");
        Assert.assertEquals("b-request", result);

        result = moduleB.doAction("proxy-action-a", "request");
        Assert.assertEquals("a-request", result);

        result = moduleA.doAction("proxy-action-b", "request");
        Assert.assertEquals("b-request", result);
    }

    public static ModuleConfig buildModuleConfig(String name, String version, boolean enabled, String path) {
        ModuleConfig moduleConfig = new ModuleConfig();
        //通过该方法构建的配置都是使用注解形式扫描bean的
        String scanBase = "com.alipay.jarslink.demo.annotation";
        moduleConfig.addScanPackage(scanBase);


        moduleConfig.withEnabled(enabled).withName(name).withVersion(version).withOverridePackages(ImmutableList.of
                ("com.alipay.jarslink.demo"));
        URL demoModule = Thread.currentThread().getContextClassLoader().getResource(path);

        moduleConfig.setModuleUrl(ImmutableList.of(demoModule));
        return moduleConfig;
    }
}
