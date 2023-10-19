/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.producer.demo.service;

import com.xkw.bcom.rocketmq.core.message.XkwMessageType;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessage;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessagesBuilder;
import com.xkw.bcom.rocketmq.core.producer.XkwMessageSender;
import com.xkw.bcom.rocketmq.producer.demo.model.TestModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * OrderMessageService
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年11月08日
 */
@Service
public class OrderMessageService {

    @Value("${topic}")
    private String topic;
    @Resource
    private XkwMessageSender messageSender;

    @Transactional
    public void send() {
        List<XkwProducerMessage> messages1 = createMessages("111111111");
        List<XkwProducerMessage> messages2 = createMessages("222222222");
        List<XkwProducerMessage> messages3 = createMessages("333333333");
        List<XkwProducerMessage> messages4 = createMessages("444444444");
        List<XkwProducerMessage> messages5 = createMessages("555555555");
        List<XkwProducerMessage> messages6 = createMessages("666666666");
        List<XkwProducerMessage> messages7 = createMessages("777777777");
        List<XkwProducerMessage> messages8 = createMessages("888888888");
        List<XkwProducerMessage> messages9 = createMessages("999999999");
        messageSender.sendMessages(messages1);
        messageSender.sendMessages(messages2);
        messageSender.sendMessages(messages3);
        messageSender.sendMessages(messages4);
        messageSender.sendMessages(messages5);
        messageSender.sendMessages(messages6);
        messageSender.sendMessages(messages7);
        messageSender.sendMessages(messages8);
        messageSender.sendMessages(messages9);
    }

    private List<XkwProducerMessage> createMessages(String hashKey) {
        return new XkwProducerMessagesBuilder()
                .topic(topic)
                .tag("order")
                .type(XkwMessageType.ORDER)
                .hashKey(hashKey)
                .addPayload(new TestModel(1, hashKey, "2022"))
                .addPayload(new TestModel(2, hashKey, "2022"))
                .addPayload(new TestModel(3, hashKey, "2022"))
                .addPayload(new TestModel(4, hashKey, "2022"))
                .addPayload(new TestModel(5, hashKey, "2022"))
                .addPayload(new TestModel(6, hashKey, "2022"))
                .addPayload(new TestModel(7, hashKey, "2022"))
                .addPayload(new TestModel(8, hashKey, "2022"))
                .addPayload(new TestModel(9, hashKey, "2022"))
                .build();
    }
}
