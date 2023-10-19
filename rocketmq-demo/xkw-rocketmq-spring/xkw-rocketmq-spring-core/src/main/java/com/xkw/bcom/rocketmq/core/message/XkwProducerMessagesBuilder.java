/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.message;

import com.xkw.bcom.rocketmq.core.constant.XkwRocketmqConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * XkwProducerMessagesBuilder
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月23日
 */
public class XkwProducerMessagesBuilder {

    private String topic;
    private String tag;
    private XkwMessageType type;
    private String hashKey;
    private Integer delayLevel;
    private List<Object> payloads;

    public XkwProducerMessagesBuilder() {
        this.type = XkwMessageType.CONCURRENT;
        this.hashKey = XkwRocketmqConstant.BLANK;
        this.delayLevel = 0;
    }

    public XkwProducerMessagesBuilder topic(String topic) {
        this.topic = topic;
        return this;
    }

    public XkwProducerMessagesBuilder tag(String tag) {
        this.tag = tag;
        return this;
    }

    public XkwProducerMessagesBuilder type(XkwMessageType type) {
        this.type = type;
        return this;
    }

    public XkwProducerMessagesBuilder hashKey(String hashKey) {
        this.hashKey = hashKey;
        return this;
    }

    public XkwProducerMessagesBuilder delayLevel(Integer delayLevel) {
        this.delayLevel = delayLevel;
        return this;
    }

    public XkwProducerMessagesBuilder payloads(List<Object> payloads) {
        this.payloads = payloads;
        return this;
    }

    public XkwProducerMessagesBuilder addPayload(Object payload) {
        if (this.payloads == null) {
            this.payloads = new ArrayList<>();
        }
        this.payloads.add(payload);
        return this;
    }

    public List<XkwProducerMessage> build() {
        if (this.payloads == null || this.payloads.isEmpty()) {
            return null;
        }
        List<XkwProducerMessage> messages = new ArrayList<>(this.payloads.size());
        for (Object payload : this.payloads) {
            XkwProducerMessage message = new XkwProducerMessage(topic, tag, type, hashKey, delayLevel, payload);
            messages.add(message);
        }
        return messages;
    }
}
