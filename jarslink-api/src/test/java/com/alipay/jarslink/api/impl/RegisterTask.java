package com.alipay.jarslink.api.impl;

import com.alipay.jarslink.api.Module;

/**
 * @author joe
 * @version 2018.04.02 00:15
 */
public class RegisterTask implements Runnable {
    private ConcurrentModuleManagerImpl concurrentModuleManager;
    private Module m1;

    public RegisterTask(ConcurrentModuleManagerImpl concurrentModuleManager, Module m1) {
        this.concurrentModuleManager = concurrentModuleManager;
        this.m1 = m1;
    }

    @Override
    public void run() {
        concurrentModuleManager.register(m1);
    }
}
