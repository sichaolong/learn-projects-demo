/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.producer.demo.controller;

import com.xkw.bcom.rocketmq.core.message.XkwMessageType;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessage;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessageBuilder;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessagesBuilder;
import com.xkw.bcom.rocketmq.core.producer.XkwMessageSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * StringController
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月28日
 */
@RestController
@RequestMapping("/string")
public class StringController {

    @Value("${topic}")
    private String topic;
    @Resource
    private XkwMessageSender messageSender;

    @GetMapping("/concurrent")
    public void concurrent() {
        XkwProducerMessage message = new XkwProducerMessageBuilder()
                .topic(topic)
                .tag("string")
                .payload("concurrent string message")
                .build();
        messageSender.sendMessage(message);
    }

    @GetMapping("/delay")
    public void delay() {
        XkwProducerMessage message = new XkwProducerMessageBuilder()
                .topic(topic)
                .tag("string")
                .type(XkwMessageType.DELAY)
                .payload("delay string message")
                .delayLevel(5)
                .build();
        messageSender.sendMessage(message);
    }

    @GetMapping("/batch")
    public void batch() {
        List<XkwProducerMessage> messages = new XkwProducerMessagesBuilder()
                .topic(topic)
                .tag("string")
                .addPayload("concurrent string message 1")
                .addPayload("concurrent string message 2")
                .addPayload("concurrent string message 3")
                .addPayload("concurrent string message 4")
                .addPayload("concurrent string message 5")
                .addPayload("concurrent string message 6")
                .addPayload("concurrent string message 7")
                .addPayload("concurrent string message 8")
                .addPayload("concurrent string message 9")
                .addPayload("concurrent string message 10")
                .addPayload("concurrent string message 11")
                .addPayload("concurrent string message 12")
                .addPayload("concurrent string message 13")
                .addPayload("concurrent string message 14")
                .addPayload("concurrent string message 15")
                .addPayload("concurrent string message 16")
                .addPayload("concurrent string message 17")
                .addPayload("concurrent string message 18")
                .addPayload("concurrent string message 19")
                .addPayload("concurrent string message 20")
                .addPayload("concurrent string message 21")
                .addPayload("concurrent string message 22")
                .addPayload("concurrent string message 23")
                .addPayload("concurrent string message 24")
                .addPayload("concurrent string message 25")
                .addPayload("concurrent string message 26")
                .addPayload("concurrent string message 27")
                .addPayload("concurrent string message 28")
                .addPayload("concurrent string message 29")
                .build();
        messageSender.sendMessages(messages);
    }
}
