/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.producer.demo.controller;

import com.xkw.bcom.rocketmq.producer.demo.service.OrderMessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * OrderController
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年07月05日
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderMessageService orderMessageService;

    @GetMapping
    public void send() {
        // 验证单节点消费者，顺序消费的线程数
        orderMessageService.send();
    }
}
