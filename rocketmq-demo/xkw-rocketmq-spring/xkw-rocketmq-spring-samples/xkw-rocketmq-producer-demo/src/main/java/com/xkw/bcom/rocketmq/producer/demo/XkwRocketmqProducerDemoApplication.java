/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.producer.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * XkwRocketmqProducerDemoApplication
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月23日
 */
@SpringBootApplication
@EnableTransactionManagement
public class XkwRocketmqProducerDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(XkwRocketmqProducerDemoApplication.class, args);
    }
}
