package com.alipay.jarslink.api.impl;

import com.alipay.jarslink.api.Module;
import com.alipay.jarslink.api.ModuleListener;

public class ModuleListenerImpl implements ModuleListener {

	private final ListenerContext context;
	public ModuleListenerImpl(ListenerContext context) {
		super();
		this.context = context;
	}

	@Override
	public void onRegistered(Module module) {
		context.setInvokeRegistered(true);
	}
	
	@Override
	public void onPreDestroy(Module module) {
		context.setInvokePreDestroy(true);
	}
	
	@Override
	public void onLoaded(Module module) {
		context.setInvokeLoaded(true);
	}
	
	@Override
	public void onDeregistered(Module module) {
		context.setInvokeDeregistered(true);
	}
}
