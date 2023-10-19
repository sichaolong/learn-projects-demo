/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.producer.demo.controller;

import com.xkw.bcom.rocketmq.core.message.XkwMessageType;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessage;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessageBuilder;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessagesBuilder;
import com.xkw.bcom.rocketmq.core.producer.XkwMessageSender;
import com.xkw.bcom.rocketmq.producer.demo.model.TestModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * ObjectController
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月28日
 */
@RestController
@RequestMapping("/object")
public class ObjectController {

    @Value("${topic}")
    private String topic;
    @Resource
    private XkwMessageSender messageSender;

    @GetMapping("/concurrent")
    public void concurrent() {
        XkwProducerMessage message = new XkwProducerMessageBuilder()
                .topic(topic)
                .tag("object")
                .payload(new TestModel(1, "concurrent object message", "2022"))
                .build();
        messageSender.sendMessage(message);
    }

    @GetMapping("/delay")
    public void delay() {
        XkwProducerMessage message = new XkwProducerMessageBuilder()
                .topic(topic)
                .tag("object")
                .type(XkwMessageType.DELAY)
                .payload(new TestModel(2, "delay message", "2022"))
                .delayLevel(5)
                .build();
        messageSender.sendMessage(message);
    }

    @GetMapping("/batch")
    public void batch() {
        List<XkwProducerMessage> messages = new XkwProducerMessagesBuilder()
                .topic(topic)
                .tag("object")
                .addPayload(new TestModel(1, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(2, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(3, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(4, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(5, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(6, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(7, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(8, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(9, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(10, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(11, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(12, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(13, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(14, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(15, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(16, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(17, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(18, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(19, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(20, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(21, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(22, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(23, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(24, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(25, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(26, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(27, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(28, "concurrent object batch message", "2022"))
                .addPayload(new TestModel(29, "concurrent object batch message", "2022"))
                .build();
        messageSender.sendMessages(messages);
    }

    @GetMapping("/list")
    public void list() {
        List<XkwProducerMessage> messages = new XkwProducerMessagesBuilder()
                .topic(topic)
                .tag("objectList")
                .addPayload(Collections.singletonList(new TestModel(1, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(2, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(3, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(4, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(5, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(6, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(7, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(8, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(9, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(10, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(11, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(12, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(13, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(14, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(15, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(16, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(17, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(18, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(19, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(20, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(21, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(22, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(23, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(24, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(25, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(26, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(27, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(28, "concurrent object batch message", "2022")))
                .addPayload(Collections.singletonList(new TestModel(29, "concurrent object batch message", "2022")))
                .build();
        messageSender.sendMessages(messages);
    }
}
