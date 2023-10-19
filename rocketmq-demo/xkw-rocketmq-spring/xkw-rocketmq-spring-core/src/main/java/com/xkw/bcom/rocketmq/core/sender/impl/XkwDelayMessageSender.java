/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.sender.impl;

import com.xkw.bcom.rocketmq.core.constant.XkwRocketmqConstant;
import com.xkw.bcom.rocketmq.core.message.XkwMessageType;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessage;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * XkwDelayMessageSender
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月17日
 */
@MessageType(XkwMessageType.DELAY)
public class XkwDelayMessageSender extends AbstractXkwMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(XkwDelayMessageSender.class);

    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Resource
    private RocketMQProperties rocketMQProperties;

    @Override
    public Map<TrySendResult, List<XkwProducerMessage>> trySend(List<MessageWrapper> wrappers) {
        // 延迟消息不支持批量发送，遍历逐个发送
        return wrappers.parallelStream()
                .map(wrapper -> {
                    TrySendResult result = trySendOne(wrapper);
                    return Pair.of(result, wrapper.getProducerMessage());
                })
                .collect(Collectors.groupingBy(Pair::getLeft, Collectors.mapping(Pair::getRight, Collectors.toList())));
    }

    public TrySendResult trySendOne(MessageWrapper wrapper) {
        String topic = wrapper.getProducerMessage().getTopic();
        String tag = wrapper.getProducerMessage().getTag();
        String destination = String.format(XkwRocketmqConstant.DESTINATION, topic, tag);
        try {
            int sendTimeout = rocketMQProperties.getProducer().getSendMessageTimeout();
            int delayLevel = wrapper.getProducerMessage().getDelayLevel();
            SendResult result = rocketMQTemplate.syncSend(destination, wrapper.getMessage(), sendTimeout, delayLevel);
            SendStatus status = result.getSendStatus();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("destination: {}, status: {}, id: {}", destination, status, wrapper.getProducerMessage().getId());
            }
            if (status == SendStatus.SEND_OK) {
                return TrySendResult.SUCCESS;
            }
            return TrySendResult.FAILURE;
        } catch (Throwable e) {
            LOGGER.error("send delay message failure", e);
            return TrySendResult.FAILURE;
        }
    }
}
