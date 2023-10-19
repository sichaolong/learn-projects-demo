/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xkw.bcom.rocketmq.core.message.XkwConsumerMessage;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * IMessageHandler
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年08月15日
 */
interface IMessageHandler<T> {

    TypeReference<T> payloadType();

    void handleMessage(MessageExt messageExt, String namespace, long consumeTimeout);

    void handleMessage(T payload, XkwConsumerMessage consumerMessage, MessageExt messageExt);
}
