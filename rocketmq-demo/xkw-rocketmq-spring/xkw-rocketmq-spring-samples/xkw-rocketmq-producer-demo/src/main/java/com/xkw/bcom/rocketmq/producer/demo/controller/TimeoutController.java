/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.producer.demo.controller;

import com.xkw.bcom.rocketmq.core.message.XkwProducerMessage;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessageBuilder;
import com.xkw.bcom.rocketmq.core.producer.XkwMessageSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * TimeoutController
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年11月14日
 */
@RestController
@RequestMapping("/timeout")
public class TimeoutController {

    @Value("${topic}")
    private String topic;
    @Resource
    private XkwMessageSender messageSender;

    @GetMapping
    public void sendOne() {
        XkwProducerMessage message = new XkwProducerMessageBuilder()
                .topic(topic)
                .tag("timeout")
                .payload("timeout string message")
                .build();
        messageSender.sendMessage(message);
    }
}
