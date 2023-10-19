/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.sender.impl;

import com.xkw.bcom.rocketmq.core.message.XkwProducerMessage;
import org.springframework.messaging.Message;

/**
 * MessageWrapper
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月17日
 */
class MessageWrapper {

    private Message<String> message;
    private XkwProducerMessage producerMessage;

    public MessageWrapper() {
    }

    public MessageWrapper(Message<String> message, XkwProducerMessage producerMessage) {
        this.message = message;
        this.producerMessage = producerMessage;
    }

    public Message<String> getMessage() {
        return message;
    }

    public void setMessage(Message<String> message) {
        this.message = message;
    }

    public XkwProducerMessage getProducerMessage() {
        return producerMessage;
    }

    public void setProducerMessage(XkwProducerMessage producerMessage) {
        this.producerMessage = producerMessage;
    }
}
