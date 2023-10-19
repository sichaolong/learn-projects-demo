/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.producer;

import com.xkw.bcom.rocketmq.core.constant.XkwRocketmqConstant;
import com.xkw.bcom.rocketmq.core.message.XkwMessageType;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessage;
import com.xkw.bcom.rocketmq.core.repository.XkwRocketmqProducerRepository;
import com.xkw.bcom.rocketmq.core.serializer.XkwRocketmqSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * XkwMessageSender
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月15日
 */
public class XkwMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(XkwMessageSender.class);

    @Resource
    private PlatformTransactionManager transactionManager;
    @Resource
    private XkwRocketmqProducerRepository producerRepository;
    @Resource
    private XkwMessageSenderExecutor defaultMessageSenderExecutor;
    @Resource
    private XkwMessageSenderExecutor orderMessageSenderExecutor;
    @Resource
    private XkwRocketmqSerializer<String> serializer;
    private String keyPrefix;
    private String namespace;

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * 发送一条消息，使用事务同步器实现提交事务时立即发送消息，事务同步器在开启事务时才能生效
     * <p>如果不在事务中调用，则会开启默认传播机制和隔离级别的事务，在事务中保存消息并提交事务
     *
     * @param message 生产者消息
     */
    public void sendMessage(XkwProducerMessage message) {
        fillProperties(message);
        TransactionStatus status = null;
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            status = transactionManager.getTransaction(XkwRocketmqConstant.TRANSACTION_DEFINITION);
        }
        try {
            Long id = producerRepository.save(message);
            if (id == null) {
                return;
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("save message, id: {}, keyPrefix: {}, namespace: {}, topic: {}, tag: {}, type: {}, hashKey: {}, delayLevel: {}, payload: {}",
                        id, message.getKeyPrefix(), message.getNamespace(), message.getTopic(), message.getTag(),
                        message.getType(), message.getHashKey(), message.getDelayLevel(), message.getPayload());
            }
            message.setId(id);
            add(message);
        } catch (Throwable e) {
            if (status != null) {
                transactionManager.rollback(status);
            }
            throw e;
        }
        if (status != null) {
            transactionManager.commit(status);
        }
    }

    /**
     * 发送一批消息，使用事务同步器实现提交事务时立即发送消息，事务同步器在开启事务时才能生效
     * <p>如果不在事务中调用，则会开启默认传播机制和隔离级别的事务，在事务中保存消息并提交事务
     *
     * @param messages 生产者消息集合
     */
    public void sendMessages(List<XkwProducerMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }
        fillProperties(messages);
        TransactionStatus status = null;
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            status = transactionManager.getTransaction(XkwRocketmqConstant.TRANSACTION_DEFINITION);
        }
        try {
            List<Long> ids = producerRepository.saveBatch(messages);
            if (ids == null || ids.isEmpty()) {
                return;
            }
            for (int i = 0; i < messages.size(); i++) {
                XkwProducerMessage message = messages.get(i);
                Long id = ids.get(i);
                message.setId(id);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("save message, id: {}, keyPrefix: {}, namespace: {}, topic: {}, tag: {}, type: {}, hashKey: {}, delayLevel: {}, payload: {}",
                            id, message.getKeyPrefix(), message.getNamespace(), message.getTopic(), message.getTag(),
                            message.getType(), message.getHashKey(), message.getDelayLevel(), message.getPayload());
                }
            }
            addAll(messages);
        } catch (Throwable e) {
            if (status != null) {
                transactionManager.rollback(status);
            }
            throw e;
        }
        if (status != null) {
            transactionManager.commit(status);
        }
    }

    private void fillProperties(XkwProducerMessage message) {
        String payload = serializer.serialize(message.getPayloadObject());
        message.setPayload(payload);
        message.setKeyPrefix(keyPrefix);
        message.setNamespace(namespace);
    }

    private void fillProperties(List<XkwProducerMessage> messages) {
        messages.forEach(this::fillProperties);
    }

    private void add(XkwProducerMessage message) {
        findOrInit().add(message);
    }

    private void addAll(List<XkwProducerMessage> messages) {
        findOrInit().addAll(messages);
    }

    /**
     * 获取与当前事务绑定的{@link MessageTransactionSynchronization}，或初始化此类实例绑定到当前事务
     */
    private MessageTransactionSynchronization findOrInit() {
        List<TransactionSynchronization> synchronizations = TransactionSynchronizationManager.getSynchronizations();
        for (TransactionSynchronization synchronization : synchronizations) {
            if (synchronization instanceof MessageTransactionSynchronization) {
                return (MessageTransactionSynchronization) synchronization;
            }
        }
        MessageTransactionSynchronization synchronization = new MessageTransactionSynchronization();
        TransactionSynchronizationManager.registerSynchronization(synchronization);
        return synchronization;
    }

    class MessageTransactionSynchronization extends TransactionSynchronizationAdapter {

        /**
         * 当前事务提交的消息集合
         */
        private final List<XkwProducerMessage> list = new ArrayList<>();

        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE;
        }

        @Override
        public void afterCommit() {
            // 事务同步器其实是个钩子，会同步调用，为了不阻塞业务，在线程中发送消息
            if (list.isEmpty()) {
                return;
            }
            // 将消息按照是否顺序消息分一下组
            Map<Boolean, List<XkwProducerMessage>> messageMap = list.stream()
                    .collect(Collectors.partitioningBy(message -> message.getType() == XkwMessageType.ORDER));
            List<XkwProducerMessage> orderMessages = messageMap.get(true);
            List<XkwProducerMessage> disorderMessages = messageMap.get(false);
            if (orderMessages != null && !orderMessages.isEmpty()) {
                orderMessageSenderExecutor.execute(orderMessages);
            }
            if (disorderMessages != null && !disorderMessages.isEmpty()) {
                defaultMessageSenderExecutor.execute(disorderMessages);
            }
        }

        public void add(XkwProducerMessage message) {
            list.add(message);
        }

        public void addAll(List<XkwProducerMessage> messages) {
            list.addAll(messages);
        }
    }
}
