/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package me.qlong.tech.service.impl;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import me.qlong.tech.service.SampleJvmService;
import org.springframework.stereotype.Component;

/**
 * @author qilong.zql 18/6/13-上午11:17
 */
@SofaService
@Component
public class AppTwoSampleService implements SampleJvmService{
    public String service() {
        return "App Two";
    }
}