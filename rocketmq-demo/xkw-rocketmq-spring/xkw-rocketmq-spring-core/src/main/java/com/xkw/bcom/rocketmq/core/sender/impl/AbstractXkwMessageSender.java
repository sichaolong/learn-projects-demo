/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.sender.impl;

import com.xkw.bcom.rocketmq.core.constant.XkwRocketmqConstant;
import com.xkw.bcom.rocketmq.core.exception.XkwRocketmqSqlException;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessage;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessageStatus;
import com.xkw.bcom.rocketmq.core.repository.XkwRocketmqProducerRepository;
import com.xkw.bcom.rocketmq.core.sender.XkwMessageSenderStrategy;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AbstractXkwMessageSender
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月17日
 */
public abstract class AbstractXkwMessageSender implements XkwMessageSenderStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractXkwMessageSender.class);

    @Resource
    private PlatformTransactionManager transactionManager;
    @Resource
    private XkwRocketmqProducerRepository producerRepository;

    @Override
    public boolean send(List<XkwProducerMessage> producerMessages) {
        // 1. 开启事务
        TransactionStatus status = transactionManager.getTransaction(XkwRocketmqConstant.TRANSACTION_DEFINITION);
        try {
            // 2. 将消息标记为成功
            markSuccess(producerMessages);
        } catch (XkwRocketmqSqlException e) {
            // 数据库异常
            LOGGER.error("database error", e);
            transactionManager.rollback(status);
            return false;
        }
        // 3. 发送消息
        List<MessageWrapper> wrappers = producerMessages.stream()
                .map(producerMessage -> {
                    String key;
                    if (StringUtils.isNotBlank(producerMessage.getNamespace())) {
                        key = producerMessage.getNamespace() + XkwRocketmqConstant.NAMESPACE_SUFFIX
                                + producerMessage.getKeyPrefix() + producerMessage.getId();
                    } else {
                        key = producerMessage.getKeyPrefix() + producerMessage.getId();
                    }
                    Message<String> message = MessageBuilder.withPayload(producerMessage.getPayload())
                            .setHeader(RocketMQHeaders.KEYS, key)
                            .build();
                    return new MessageWrapper(message, producerMessage);
                })
                .collect(XkwRocketmqConstant.toList(producerMessages.size()));
        Map<TrySendResult, List<XkwProducerMessage>> resultMap = trySend(wrappers);
        for (Map.Entry<TrySendResult, List<XkwProducerMessage>> entry : resultMap.entrySet()) {
            // 4. 根据发送结果处理消息状态
            try {
                switch (entry.getKey()) {
                    case FAILURE:
                        // 将消息标记为失败，并增加重试次数
                        markFailureAndIncreaseRetryTimes(entry.getValue());
                        break;
                    case UN_SEND:
                        // 将消息标记为失败
                        markFailure(entry.getValue());
                        break;
                }
            } catch (XkwRocketmqSqlException e) {
                // 数据库异常
                LOGGER.error("database error", e);
                transactionManager.rollback(status);
                return false;
            }
        }
        // 5. 提交事务
        transactionManager.commit(status);
        // 如果没有失败消息就返回true
        return CollectionUtils.isEmpty(resultMap.get(TrySendResult.FAILURE));
    }

    /**
     * 发送MQ消息，返回一个map
     * <p>key为发送结果
     * <p>value为该结果对应的消息集合
     */
    protected abstract Map<TrySendResult, List<XkwProducerMessage>> trySend(List<MessageWrapper> wrappers);

    protected void markSuccess(List<XkwProducerMessage> producerMessages) {
        List<Long> ids = producerMessages.stream()
                .map(XkwProducerMessage::getId)
                .collect(XkwRocketmqConstant.toList(producerMessages.size()));
        producerRepository.updateStatusAndCommitTimeByIds(ids, XkwProducerMessageStatus.P2, System.currentTimeMillis());
    }

    protected void markFailureAndIncreaseRetryTimes(List<XkwProducerMessage> producerMessages) {
        List<Long> ids = producerMessages.stream()
                .map(XkwProducerMessage::getId)
                .collect(XkwRocketmqConstant.toList(producerMessages.size()));
        producerRepository.updateStatusAndIncreaseRetryTimesByIds(ids, XkwProducerMessageStatus.P1);
    }

    protected void markFailure(List<XkwProducerMessage> producerMessages) {
        List<Long> ids = producerMessages.stream()
                .filter(producerMessage -> producerMessage.getStatus() != XkwProducerMessageStatus.P1)
                .map(XkwProducerMessage::getId)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        producerRepository.updateStatusByIds(ids, XkwProducerMessageStatus.P1);
    }
}
