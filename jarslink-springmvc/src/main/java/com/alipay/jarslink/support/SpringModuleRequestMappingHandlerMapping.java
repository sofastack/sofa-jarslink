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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils.MethodFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.HandlerMethodSelector;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.alipay.jarslink.api.Module;
import com.alipay.jarslink.api.ModuleListener;
import com.alipay.jarslink.api.ModuleListenerDispatcher;
import com.alipay.jarslink.api.impl.SpringModule;

/**
 * spring MVC RequestMapping 注解请求处理类，用于jarslink集成模块中的RequestMapping注解，使得jarslink可使用spring mvc的方式进行开发。
 * <br/>
 * 使用说明：需要在spring mvc的配置文件中配置  SpringModuleRequestMappingHandlerMapping
 * <br/><b>由于unregisterMapping方法在spring mvc 4.2+版本才有，因此需要使用spring mvc 4.2+版本</b>
 * @author liangruisen
 */
public class SpringModuleRequestMappingHandlerMapping extends RequestMappingHandlerMapping implements ModuleListener {

	private final Map<Module, List<RequestMappingInfo>> modulesMap = new HashMap<Module, List<RequestMappingInfo>>(20);

	private ModuleListenerDispatcher listenerDispatcher;
	
	private final ThreadLocal<ApplicationContext> currentContextLocal = new InheritableThreadLocal<ApplicationContext>();

	@Override
	public void afterPropertiesSet() {
		listenerDispatcher.addModuleListener(this);
		super.afterPropertiesSet();
	}

	/**
	 * 重写initHandlerMethods方法，忽略启动注册RequestMapping
	 */
	@Override
	protected void initHandlerMethods() {

	}

	/**
	 * 根据模块获取RequestMappingInfo列表，用于卸载模块时移除干净
	 * @param module
	 * @return List<RequestMappingInfo>
	 */
	private List<RequestMappingInfo> getMappingInfos(Module module) {
		List<RequestMappingInfo> list = modulesMap.get(module);
		if (list == null) {
			list = new ArrayList<RequestMappingInfo>(30);
			modulesMap.put(module, list);
		}
		return list;
	}

	@Override
	public void onLoaded(Module module) {
		
	}

	/**
	 * 扫描并注册RequestMapping方法
	 * @param context
	 * @param module
	 */
	protected void initHandlerMethods(ApplicationContext context, Module module) {
		String[] beanNames = context.getBeanNamesForType(Object.class);
		for (String beanName : beanNames) {
			if (!beanName.startsWith("scopedTarget.")) {
				Class<?> beanType = null;
				try {
					beanType = context.getType(beanName);
				} catch (Throwable ex) {
					// An unresolvable bean type, probably from a lazy bean - let's ignore it.
					if (logger.isDebugEnabled()) {
						logger.debug("Could not resolve target class for bean with name '" + beanName + "'", ex);
					}
				}
				if (beanType != null && isHandler(beanType)) {
					detectHandlerMethods(context, beanName, module);
				}
			}
		}
		handlerMethodsInitialized(getHandlerMethods());
	}
	
	/**
	 * @see org.springframework.web.servlet.handler.AbstractHandlerMethodMapping#createHandlerMethod(java.lang.Object, java.lang.reflect.Method)
	 */
	@Override
	protected HandlerMethod createHandlerMethod(Object handler, Method method) {
		HandlerMethod handlerMethod;
		if (handler instanceof String) {
			String beanName = (String) handler;
			ApplicationContext context = currentContextLocal.get();
			handlerMethod = new HandlerMethod(beanName, context.getAutowireCapableBeanFactory(), method);
		}
		else {
			handlerMethod = new HandlerMethod(handler, method);
		}
		return handlerMethod;
	}
	
	/**
	 * 查找Controller类里面的RequestMapping方法并进行注册
	 * @param context
	 * @param handler
	 * @param module
	 */
	protected void detectHandlerMethods(ApplicationContext context, final Object handler, Module module) {
		Class<?> handlerType = (handler instanceof String ? context.getType((String) handler) : handler.getClass());
		final Class<?> userType = ClassUtils.getUserClass(handlerType);
		final Map<Method, RequestMappingInfo> mappings = new IdentityHashMap<Method, RequestMappingInfo>();
		Set<Method> methods = HandlerMethodSelector.selectMethods(userType, new MethodFilter() {
			@Override
			public boolean matches(Method method) {
				RequestMappingInfo mapping = getMappingForMethod(method, userType);
				if (mapping != null) {
					mappings.put(method, mapping);
					return true;
				}
				else {
					return false;
				}
			}
		});
		if (logger.isDebugEnabled()) {
			logger.debug(methods.size() + " request handler methods found on " + userType + ": " + methods);
		}
		for (Method method : methods) {
			RequestMappingInfo mapping = mappings.get(method);
			getMappingInfos(module).add(mapping);
			registerMapping(mapping, handler, method);
		}
	}

	@Override
	public void onPreDestroy(Module module) {
		onDeregistered(module);
	}

	@Resource
	public void setModuleListenerDispatcher(ModuleListenerDispatcher dispatcher) {
		this.listenerDispatcher = dispatcher;
	}

	/**
	 * 模块注册之后调用，扫描并注册RequestMapping
	 * @see com.alipay.jarslink.api.ModuleListener#onRegistered(com.alipay.jarslink.api.Module)
	 */
	@Override
	public void onRegistered(Module module) {
		if (!(module instanceof SpringModule)) {
			throw new IllegalArgumentException(
					module.getName() + "-" + module.getVersion() + " is not an SpringModule.");
		}
		SpringModule springModule = (SpringModule) module;
		ApplicationContext context = springModule.getApplicationContext();
		synchronized (this) {
			currentContextLocal.set(context);
			initHandlerMethods(context, module);
			currentContextLocal.remove();
		}
	}

	/**
	 * 当模块解除注册之后调用，用来移除模块的RequestMapping方法
	 * @see com.alipay.jarslink.api.ModuleListener#onDeregistered(com.alipay.jarslink.api.Module)
	 */
	@Override
	public void onDeregistered(Module module) {
		List<RequestMappingInfo> list = modulesMap.get(module);
		if (list != null) {
			for (RequestMappingInfo info : list) {
				unregisterMapping(info);
			}
		}
		modulesMap.remove(module);
	}
}
