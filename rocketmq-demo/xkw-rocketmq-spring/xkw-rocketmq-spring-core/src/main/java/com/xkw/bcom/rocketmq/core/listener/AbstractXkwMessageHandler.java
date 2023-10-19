/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.listener;

import com.xkw.bcom.rocketmq.core.message.XkwConsumerMessage;
import com.xkw.bcom.rocketmq.core.serializer.XkwRocketmqSerializer;
import com.xkw.bcom.rocketmq.core.service.XkwRocketmqMessageService;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * AbstractXkwMessageHandler
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年08月15日
 */
public abstract class AbstractXkwMessageHandler<T> extends SuperXkwMessageHandler<T> implements IMessageHandler<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractXkwMessageHandler.class);

    @Resource
    private XkwRocketmqMessageService messageService;
    @Resource
    private XkwRocketmqSerializer<String> serializer;

    @Override
    public void handleMessage(MessageExt messageExt, String namespace, long consumeTimeout) {
        XkwConsumerMessage message = messageService.parseMessage(messageExt, namespace);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("consume message, id: {}, uniqueKey: {}, namespace: {}, topic: {}, tag: {}, payload: {}",
                    message.getId(), message.getUniqueKey(), message.getNamespace(), message.getTopic(),
                    message.getTag(), message.getPayload());
        }
        T body = serializer.deserialize(message.getPayload(), payloadType());
        this.handleMessage(body, message, messageExt);
    }
}
