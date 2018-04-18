/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.jarslink.support;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.alipay.jarslink.api.Module;
import com.alipay.jarslink.api.ModuleConfig;
import com.alipay.jarslink.api.ModuleLoader;
import com.alipay.jarslink.api.ModuleManager;
import com.google.common.collect.ImmutableList;

/**
 * SpringMvc RequestMapping 集成测试
 * 
 * @author liangruisen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:META-INF/spring/jarslink-springmvc.xml" })
public class SpringMvcRequestMappingTest implements ApplicationContextAware {
	public static final String JARSLINK_MODULE_DEMO = "jarslink-module-demo-1.0.0.jar";

	private MockMvc mockMvc;

	private WebApplicationContext context;

	@Resource
	private ModuleManager moduleManager;

	@Resource
	private ModuleLoader moduleLoader;

	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@After
	public void tearDown() throws Exception {
		Module module = moduleManager.remove("springmvc-demo");
		moduleLoader.unload(module);
	}

	@Test
	public void shouldRegisteredModuleTest() throws Exception {
		Module module = moduleLoader.load(buildModuleConfig("springmvc-demo", "1.0.0", true));
		moduleManager.register(module);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/springmvc.json")
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(builder).andReturn();
		System.out.println(result.getResponse().getContentAsString());
		Assert.assertEquals("{\"name\"=\"springmvc\"}", result.getResponse().getContentAsString());
	}
	
	@Test
	public void shouldDeregisteredModuleTest() throws Exception {
		Module module = moduleLoader.load(buildModuleConfig("springmvc-demo", "1.0.0", true));
		moduleManager.register(module);
		module = moduleManager.remove("springmvc-demo");
		moduleLoader.unload(module);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/springmvc.json")
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(builder).andReturn();
		System.out.println(result.getResponse().getContentAsString());
		try {
			Assert.assertEquals("{\"name\"=\"springmvc\"}", result.getResponse().getContentAsString());
		}catch(Throwable e) {
			Assert.assertNotNull(e);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = (WebApplicationContext) context;
	}

	public static ModuleConfig buildModuleConfig(String name, String version, boolean enabled) {
		URL demoModule;
		ModuleConfig moduleConfig = new ModuleConfig();
		// 通过该方法构建的配置都是使用注解形式扫描bean的
		String scanBase = "com.alipay.jarslink.springmvc.demo";
		moduleConfig.addScanPackage(scanBase);
		Map<String, Object> properties = new HashMap<String, Object>();
		moduleConfig.withEnabled(enabled).withVersion(version).withProperties(properties);
		demoModule = Thread.currentThread().getContextClassLoader().getResource(JARSLINK_MODULE_DEMO);
		moduleConfig.setName(name);
		moduleConfig.setEnabled(enabled);
		moduleConfig.setVersion(version);
		moduleConfig.setProperties(properties);
		moduleConfig.setModuleUrl(ImmutableList.of(demoModule));
		return moduleConfig;
	}
}
