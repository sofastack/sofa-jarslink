package com.alipay.jarslink.api.impl;

import com.alipay.jarslink.api.Module;
import com.alipay.jarslink.api.ModuleConfig;
import com.alipay.jarslink.api.ModuleLoader;
import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author joe
 * @version 2018.04.01 22:58
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/jarslink.xml"})
public class ConcurrentModuleManagerImplTest {
    public static final String JARSLINK_MODULE_DEMO = "jarslink-module-demo-1.0.0.jar";
    @Autowired
    private ConcurrentModuleManagerImpl testModuleManager;
    @Autowired
    private ModuleLoader moduleLoader;


    @Test
    public void concurrentTest() {
        testModuleManager.activeConcurrentTest();

        Module m1 = moduleLoader.load(buildModuleConfig("1.0").withNeedUnloadOldVersion(false));
        Module m2 = moduleLoader.load(buildModuleConfig("2.0").withNeedUnloadOldVersion(false));


        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                testModuleManager.register(m1);
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                testModuleManager.register(m2);
            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Module m3 = testModuleManager.find("demo", "1.0");
        Module m4 = testModuleManager.find("demo", "2.0");

        //由于存在并发BUG，所以此处两个肯定有一个是null
        Assert.assertTrue((m3 == null && m4 != null) || (m3 != null && m4 == null));

        testModuleManager.disableConcurrentTest();
    }

    @Test
    public void noConcurrentTest() {
        Module m1 = moduleLoader.load(buildModuleConfig("1.0").withNeedUnloadOldVersion(false));
        Module m2 = moduleLoader.load(buildModuleConfig("2.0").withNeedUnloadOldVersion(false));
        testModuleManager.register(m1);
        testModuleManager.register(m2);

        m1 = testModuleManager.find("demo", "1.0");
        m2 = testModuleManager.find("demo", "2.0");

        //没有并发是表现正常，两个都能注册成功
        Assert.assertTrue(m1 != null && m2 != null);
    }

    private ModuleConfig buildModuleConfig(String version) {
        URL demoModule;
        ModuleConfig moduleConfig = new ModuleConfig();
        //通过该方法构建的配置都是使用注解形式扫描bean的
        String scanBase = "com.alipay.jarslink.main";
        moduleConfig.addScanPackage(scanBase);
        moduleConfig.removeScanPackage(scanBase);
        Map<String, Object> properties = new HashMap();
        properties.put("url", "127.0.0.1");
        moduleConfig.withEnabled(true).withVersion(version).withOverridePackages(ImmutableList.of("com.alipay" + "" +
                ".jarslink.demo")).withName("demo").withProperties(properties);
        demoModule = Thread.currentThread().getContextClassLoader().getResource(JARSLINK_MODULE_DEMO);
        moduleConfig.setModuleUrl(ImmutableList.of(demoModule));
        return moduleConfig;
    }

}
