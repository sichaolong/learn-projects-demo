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
import java.util.Collections;
import java.util.List;

/**
 * IntController
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月28日
 */
@RestController
@RequestMapping("/int")
public class IntController {

    @Value("${topic}")
    private String topic;
    @Resource
    private XkwMessageSender messageSender;

    @GetMapping("/concurrent")
    public void concurrent() {
        XkwProducerMessage message = new XkwProducerMessageBuilder()
                .topic(topic)
                .tag("int")
                .payload(1)
                .build();
        messageSender.sendMessage(message);
    }

    @GetMapping("/delay")
    public void delay() {
        XkwProducerMessage message = new XkwProducerMessageBuilder()
                .topic(topic)
                .tag("int")
                .type(XkwMessageType.DELAY)
                .payload(2)
                .delayLevel(5)
                .build();
        messageSender.sendMessage(message);
    }

    @GetMapping("/batch")
    public void batch() {
        List<XkwProducerMessage> messages = new XkwProducerMessagesBuilder()
                .topic(topic)
                .tag("int")
                .addPayload(1)
                .addPayload(2)
                .addPayload(3)
                .addPayload(4)
                .addPayload(5)
                .addPayload(6)
                .addPayload(7)
                .addPayload(8)
                .addPayload(9)
                .addPayload(10)
                .addPayload(11)
                .addPayload(12)
                .addPayload(13)
                .addPayload(14)
                .addPayload(15)
                .addPayload(16)
                .addPayload(17)
                .addPayload(18)
                .addPayload(19)
                .addPayload(20)
                .addPayload(21)
                .addPayload(22)
                .addPayload(23)
                .addPayload(24)
                .addPayload(25)
                .addPayload(26)
                .addPayload(27)
                .addPayload(28)
                .addPayload(29)
                .build();
        messageSender.sendMessages(messages);
    }

    @GetMapping("/list")
    public void list() {
        List<XkwProducerMessage> messages = new XkwProducerMessagesBuilder()
                .topic(topic)
                .tag("intList")
                .addPayload(Collections.singletonList(1))
                .addPayload(Collections.singletonList(2))
                .addPayload(Collections.singletonList(3))
                .addPayload(Collections.singletonList(4))
                .addPayload(Collections.singletonList(5))
                .addPayload(Collections.singletonList(6))
                .addPayload(Collections.singletonList(7))
                .addPayload(Collections.singletonList(8))
                .addPayload(Collections.singletonList(9))
                .addPayload(Collections.singletonList(10))
                .addPayload(Collections.singletonList(11))
                .addPayload(Collections.singletonList(12))
                .addPayload(Collections.singletonList(13))
                .addPayload(Collections.singletonList(14))
                .addPayload(Collections.singletonList(15))
                .addPayload(Collections.singletonList(16))
                .addPayload(Collections.singletonList(17))
                .addPayload(Collections.singletonList(18))
                .addPayload(Collections.singletonList(19))
                .addPayload(Collections.singletonList(20))
                .addPayload(Collections.singletonList(21))
                .addPayload(Collections.singletonList(22))
                .addPayload(Collections.singletonList(23))
                .addPayload(Collections.singletonList(24))
                .addPayload(Collections.singletonList(25))
                .addPayload(Collections.singletonList(26))
                .addPayload(Collections.singletonList(27))
                .addPayload(Collections.singletonList(28))
                .addPayload(Collections.singletonList(29))
                .build();
        messageSender.sendMessages(messages);
    }
}
