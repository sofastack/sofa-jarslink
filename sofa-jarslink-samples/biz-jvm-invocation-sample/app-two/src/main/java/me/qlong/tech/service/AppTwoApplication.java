/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package me.qlong.tech.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author qilong.zql 18/6/13-上午11:34
 */
@SpringBootApplication
public class AppTwoApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(AppTwoApplication.class);
        springApplication.run(args);
    }
}