package com.alipay.jarslink.api.impl;

import com.alipay.jarslink.api.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.Semaphore;

import static com.alipay.jarslink.api.impl.ModuleLoaderImplTest.buildModuleConfig;

/**
 * @author joe
 * @version 2018.04.04 11:38
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/jarslink.xml"})
public class ModuleLoaderInjectTest {
    @Autowired
    private ModuleLoader moduleLoader;
    private Semaphore semaphore = new Semaphore(1);

    @Test
    public void shouldRegisterApplicationContextAware() throws InterruptedException {
        semaphore.acquire();
        ApplicationContextAware aware = new ApplicationContextAwareImpl();
        moduleLoader.registerAware(aware);
        ModuleConfig config = buildModuleConfig("demo", "1.0", true);
        Module module = moduleLoader.load(config);
        Assert.assertNotNull(module);
        Assert.assertEquals("2.0", config.getVersion());
        moduleLoader.unRegisterAware(aware);
        semaphore.release();
    }

    @Test
    public void shouldRegisterModuleAware() throws InterruptedException {
        semaphore.acquire();
        ModuleAware aware = new ModuleAwareImpl();
        moduleLoader.registerAware(aware);
        ModuleConfig config = buildModuleConfig("demo", "1.0", true);
        Module module = moduleLoader.load(config);
        Assert.assertNotNull(module);
        Assert.assertEquals("2.0", module.getModuleConfig().getVersion());
        moduleLoader.unRegisterAware(aware);
        semaphore.release();
    }

    @Test
    public void shouldRegisterApplicationContextPostProcessor() throws InterruptedException {
        semaphore.acquire();
        ApplicationContextPostProcessor postProcessor = new ApplicationContextPostProcessorImpl();
        moduleLoader.registerPostProcessor(postProcessor);
        ModuleConfig config = buildModuleConfig("demo", "1.0", true);
        Module module = moduleLoader.load(config);
        Assert.assertNotNull(module);
        Assert.assertEquals("2.0", config.getVersion());
        moduleLoader.unRegisterPostProcessor(postProcessor);
        semaphore.release();
    }

    @Test
    public void shouldRegisterModulePostProcessor() throws InterruptedException {
        semaphore.acquire();
        ModulePostProcessor postProcessor = new ModulePostProcessorImpl();
        moduleLoader.registerPostProcessor(postProcessor);
        ModuleConfig config = buildModuleConfig("demo", "1.0", true);
        Module module = moduleLoader.load(config);
        Assert.assertNotNull(module);
        Assert.assertEquals("2.0", config.getVersion());
        moduleLoader.unRegisterPostProcessor(postProcessor);
        semaphore.release();
    }

    @Test
    public void shouldUnRegisterApplicationContextAware() throws InterruptedException {
        semaphore.acquire();
        ApplicationContextAware aware = new ApplicationContextAwareImpl();
        moduleLoader.registerAware(aware);
        moduleLoader.unRegisterAware(aware);

        ModuleConfig config = buildModuleConfig("demo", "1.0", true);
        Module module = moduleLoader.load(config);
        Assert.assertNotNull(module);
        Assert.assertEquals("1.0", config.getVersion());
        semaphore.release();
    }

    @Test
    public void shouldUnRegisterModuleAware() throws InterruptedException {
        semaphore.acquire();
        ModuleAware aware = new ModuleAwareImpl();
        moduleLoader.registerAware(aware);
        moduleLoader.unRegisterAware(aware);

        ModuleConfig config = buildModuleConfig("demo", "1.0", true);
        Module module = moduleLoader.load(config);
        Assert.assertNotNull(module);
        Assert.assertEquals("1.0", module.getModuleConfig().getVersion());
        semaphore.release();
    }

    @Test
    public void shouldUnRegisterApplicationContextPostProcessor() throws InterruptedException {
        semaphore.acquire();
        ApplicationContextPostProcessor postProcessor = new ApplicationContextPostProcessorImpl();
        moduleLoader.registerPostProcessor(postProcessor);
        moduleLoader.unRegisterPostProcessor(postProcessor);
        ModuleConfig config = buildModuleConfig("demo", "1.0", true);
        Module module = moduleLoader.load(config);
        Assert.assertNotNull(module);
        Assert.assertEquals("1.0", config.getVersion());
        semaphore.release();
    }

    @Test
    public void shouldUnRegisterModulePostProcessor() throws InterruptedException {
        semaphore.acquire();
        ModulePostProcessor postProcessor = new ModulePostProcessorImpl();
        moduleLoader.registerPostProcessor(postProcessor);
        moduleLoader.unRegisterPostProcessor(postProcessor);
        ModuleConfig config = buildModuleConfig("demo", "1.0", true);
        Module module = moduleLoader.load(config);
        Assert.assertNotNull(module);
        Assert.assertEquals("1.0", config.getVersion());
        semaphore.release();
    }
}
