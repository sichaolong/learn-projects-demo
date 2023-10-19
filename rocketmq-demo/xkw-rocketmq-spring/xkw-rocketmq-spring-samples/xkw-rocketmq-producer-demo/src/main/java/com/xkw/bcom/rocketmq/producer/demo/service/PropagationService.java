/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.producer.demo.service;

import com.xkw.bcom.rocketmq.core.message.XkwProducerMessage;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessageBuilder;
import com.xkw.bcom.rocketmq.core.producer.XkwMessageSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * PropagationService
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月24日
 */
@Service
public class PropagationService {

    @Value("${topic}")
    private String topic;
    @Resource
    private XkwMessageSender messageSender;
    @Resource
    private PropagationService propagationService;

    @Transactional
    public void test1() {
        XkwProducerMessage message1 = new XkwProducerMessageBuilder()
                .topic(topic)
                .tag("concurrent")
                .payload("message1")
                .build();
        messageSender.sendMessage(message1);
        propagationService.test2();
        XkwProducerMessage message2 = new XkwProducerMessageBuilder()
                .topic(topic)
                .tag("concurrent")
                .payload("message2")
                .build();
        messageSender.sendMessage(message2);
        XkwProducerMessage message3 = new XkwProducerMessageBuilder()
                .topic(topic)
                .tag("concurrent")
                .payload("message3")
                .build();
        messageSender.sendMessage(message3);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void test2() {
        XkwProducerMessage message4 = new XkwProducerMessageBuilder()
                .topic(topic)
                .tag("concurrent")
                .payload("message4")
                .build();
        messageSender.sendMessage(message4);
        XkwProducerMessage message5 = new XkwProducerMessageBuilder()
                .topic(topic)
                .tag("concurrent")
                .payload("message5")
                .build();
        messageSender.sendMessage(message5);
        XkwProducerMessage message6 = new XkwProducerMessageBuilder()
                .topic(topic)
                .tag("concurrent")
                .payload("message6")
                .build();
        messageSender.sendMessage(message6);
    }
}
