/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.sender.impl;

import com.xkw.bcom.rocketmq.core.constant.XkwRocketmqConstant;
import com.xkw.bcom.rocketmq.core.message.XkwMessageType;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessage;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.*;

/**
 * XkwOrderMessageSender
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月17日
 */
@MessageType(XkwMessageType.ORDER)
public class XkwOrderMessageSender extends AbstractXkwMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(XkwOrderMessageSender.class);

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public Map<TrySendResult, List<XkwProducerMessage>> trySend(List<MessageWrapper> wrappers) {
        // 顺序消息不支持批量发送，遍历逐个发送
        Map<TrySendResult, List<XkwProducerMessage>> resultMap = new HashMap<>(8);
        // 发送失败消息的hashKey
        Set<String> failureHashKeys = new HashSet<>();
        for (MessageWrapper wrapper : wrappers) {
            XkwProducerMessage message = wrapper.getProducerMessage();
            // 检查当前消息的hashKey有没有失败过
            if (!failureHashKeys.isEmpty() && failureHashKeys.contains(message.getHashKey())) {
                // 如果失败过，直接标记为未发送，防止消息顺序错乱
                List<XkwProducerMessage> messages = resultMap.computeIfAbsent(TrySendResult.UN_SEND, k -> new ArrayList<>());
                messages.add(message);
                continue;
            }
            // 发送消息，合并结果
            TrySendResult result = trySendOne(wrapper);
            List<XkwProducerMessage> messages = resultMap.computeIfAbsent(result, k -> new ArrayList<>());
            messages.add(message);
            if (result == TrySendResult.FAILURE) {
                // 如果发送失败，将hashKey添加到失败集合
                failureHashKeys.add(message.getHashKey());
            }
        }
        return resultMap;
    }

    public TrySendResult trySendOne(MessageWrapper wrapper) {
        String topic = wrapper.getProducerMessage().getTopic();
        String tag = wrapper.getProducerMessage().getTag();
        String destination = String.format(XkwRocketmqConstant.DESTINATION, topic, tag);
        try {
            SendResult result = rocketMQTemplate.syncSendOrderly(destination, wrapper.getMessage(),
                    wrapper.getProducerMessage().getHashKey());
            SendStatus status = result.getSendStatus();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("destination: {}, status: {}, id: {}", destination, status, wrapper.getProducerMessage().getId());
            }
            if (status == SendStatus.SEND_OK) {
                return TrySendResult.SUCCESS;
            }
            return TrySendResult.FAILURE;
        } catch (Throwable e) {
            LOGGER.error("send order message failure", e);
            return TrySendResult.FAILURE;
        }
    }
}
