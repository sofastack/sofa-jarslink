package com.alipay.jarslink.api.impl;

public class ListenerContext {
	private boolean invokeLoaded = false;
	private boolean invokeRegistered = false;
	private boolean invokeDeregistered = false;
	private boolean invokePreDestroy = false;

	public boolean isInvokeLoaded() {
		return invokeLoaded;
	}

	public void setInvokeLoaded(boolean invokeLoaded) {
		this.invokeLoaded = invokeLoaded;
	}

	public boolean isInvokeRegistered() {
		return invokeRegistered;
	}

	public void setInvokeRegistered(boolean invokeRegistered) {
		this.invokeRegistered = invokeRegistered;
	}

	public boolean isInvokeDeregistered() {
		return invokeDeregistered;
	}

	public void setInvokeDeregistered(boolean invokeDeregistered) {
		this.invokeDeregistered = invokeDeregistered;
	}

	public boolean isInvokePreDestroy() {
		return invokePreDestroy;
	}

	public void setInvokePreDestroy(boolean invokePreDestroy) {
		this.invokePreDestroy = invokePreDestroy;
	}
}
