/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package me.qlong.controller;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import me.qlong.tech.service.SampleJvmService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author qilong.zql 18/6/13-上午6:56
 */
@RestController
public class HelloController {

    @SofaReference
    private SampleJvmService sampleJvmService;

    @RequestMapping("/hello")
    public String hello() {
        return sampleJvmService.service();
    }

}