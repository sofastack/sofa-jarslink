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
package com.alipay.jarslink.api;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 模块生命监听调度期，用来调用监听
 * @author liangruisen
 */
public class ModuleListenerDispatcher {

	/**
	 * 模块生命监听器列表
	 */
	private List<ModuleListener> listeners;
	
	/**
	 * 添加模块生命监听，如果已经存在则不再重复添加
	 * @param listener
	 */
	public void addModuleListener(ModuleListener listener) {
		if(listeners == null) {
			synchronized (this) {
				if(listeners == null) {
					listeners = new CopyOnWriteArrayList<ModuleListener>();
				}
			}
		}
		if(!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	/**
	 * 移除模块生命监听
	 * @param listener
	 */
	public void reomveModuleListener(ModuleListener listener) {
		if(listeners != null) {
			listeners.remove(listener);
		}
	}
	
	/**
	 * 加载模块完成后模块生命周期监听事件处理器
	 */
	private Handler loadedHandler = new Handler() {
		@Override
		public void handle(ModuleListener listener, Module module) {
			listener.onLoaded(module);
		}
	};
	
	/**
	 * 注册模块完成后模块生命周期监听事件处理器
	 */
	private Handler registeredHandler = new Handler() {
		@Override
		public void handle(ModuleListener listener, Module module) {
			listener.onRegistered(module);
		}
	};
	
	/**
	 * 移除模块注册完成后模块生命周期监听事件处理器
	 */
	private Handler deregisteredHandler = new Handler() {
		@Override
		public void handle(ModuleListener listener, Module module) {
			listener.onDeregistered(module);
		}
	};
	
	/**
	 * 注销模块前模块生命周期监听事件处理器
	 */
	private Handler preDestroyHandler = new Handler() {
		@Override
		public void handle(ModuleListener listener, Module module) {
			listener.onPreDestroy(module);
		}
	};
	
	/**
	 * 遍历模块生命周期监听并处理相应的事件
	 * @param handler
	 * @param module
	 */
	private void onEvent(Handler handler, Module module) {
		if(listeners != null && !listeners.isEmpty()) {
			for(ModuleListener listener : listeners) {
				handler.handle(listener, module);
			}
		}
	}
	
	/**
	 * 模块加载完成后调用
	 * 
	 * @param module
	 */
	public void onLoaded(Module module){
		onEvent(loadedHandler, module);
	}
	
	/**
	 * 模块注册完成后调用
	 * @param module
	 */
	public void onRegistered(Module module){
		onEvent(registeredHandler, module);
	}
	
	/**
	 * 模块移除注册后调用
	 * @param module
	 */
	public void onDeregistered(Module module){
		onEvent(deregisteredHandler, module);
	}

	/**
	 * 模块销毁前调用
	 * 
	 * @param module
	 */
	public void onPreDestroy(Module module){
		onEvent(preDestroyHandler, module);
	}
	
	/**
	 * 处理接口
	 * @author liangruisen
	 */
	interface Handler {
		/**
		 * 处理模块生命周期监听事件
		 * @param listener
		 * @param module
		 */
		void handle(ModuleListener listener, Module module);
	}
}
