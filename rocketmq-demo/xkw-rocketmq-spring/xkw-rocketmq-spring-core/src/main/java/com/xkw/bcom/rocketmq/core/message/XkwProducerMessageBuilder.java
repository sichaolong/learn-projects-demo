/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.message;

import com.xkw.bcom.rocketmq.core.constant.XkwRocketmqConstant;

/**
 * XkwProducerMessageBuilder
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月14日
 */
public class XkwProducerMessageBuilder {

    private String topic;
    private String tag;
    private XkwMessageType type;
    private String hashKey;
    private Integer delayLevel;
    private Object payload;

    public XkwProducerMessageBuilder() {
        this.type = XkwMessageType.CONCURRENT;
        this.hashKey = XkwRocketmqConstant.BLANK;
        this.delayLevel = 0;
    }

    public XkwProducerMessageBuilder topic(String topic) {
        this.topic = topic;
        return this;
    }

    public XkwProducerMessageBuilder tag(String tag) {
        this.tag = tag;
        return this;
    }

    public XkwProducerMessageBuilder type(XkwMessageType type) {
        this.type = type;
        return this;
    }

    public XkwProducerMessageBuilder hashKey(String hashKey) {
        this.hashKey = hashKey;
        return this;
    }

    public XkwProducerMessageBuilder delayLevel(Integer delayLevel) {
        this.delayLevel = delayLevel;
        return this;
    }

    public XkwProducerMessageBuilder payload(Object payload) {
        this.payload = payload;
        return this;
    }

    public XkwProducerMessage build() {
        return new XkwProducerMessage(topic, tag, type, hashKey, delayLevel, payload);
    }
}
