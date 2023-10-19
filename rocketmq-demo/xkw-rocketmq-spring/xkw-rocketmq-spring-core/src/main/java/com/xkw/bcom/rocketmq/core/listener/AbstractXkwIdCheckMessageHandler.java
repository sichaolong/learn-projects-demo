/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.listener;

import com.xkw.bcom.rocketmq.core.exception.XkwRocketmqConsumerException;
import com.xkw.bcom.rocketmq.core.message.XkwConsumerMessage;
import com.xkw.bcom.rocketmq.core.serializer.XkwRocketmqSerializer;
import com.xkw.bcom.rocketmq.core.service.XkwRocketmqMessageService;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;

/**
 * AbstractXkwIdCheckMessageHandler
 * <p>使用唯一键做幂等性检查，无法阻止消费超时导致重新消费，进而导致不一致的情况
 * <p>如果有消费时间较长的情况，应用需自行保证幂等性，或设置较长的超时时间
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年08月15日
 */
public abstract class AbstractXkwIdCheckMessageHandler<T> extends SuperXkwMessageHandler<T> implements IMessageHandler<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractXkwIdCheckMessageHandler.class);

    @Resource
    private XkwRocketmqMessageService messageService;
    @Resource
    private XkwRocketmqSerializer<String> serializer;

    @Override
    public void handleMessage(MessageExt messageExt, String namespace, long consumeTimeout) {
        XkwConsumerMessage message = messageService.parseMessage(messageExt, namespace);
        if (messageService.shouldConsume(message, consumeTimeout)) {
            try {
                // 复制一个对象，防止message对象被篡改，影响后续的判断
                XkwConsumerMessage consumerMessageCopy = new XkwConsumerMessage();
                BeanUtils.copyProperties(message, consumerMessageCopy);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("consume message, id: {}, uniqueKey: {}, namespace: {}, topic: {}, tag: {}, payload: {}",
                            message.getId(), message.getUniqueKey(), message.getNamespace(), message.getTopic(),
                            message.getTag(), message.getPayload());
                }
                messageService.cleanErrorLog(message);
                T payload = serializer.deserialize(message.getPayload(), payloadType());
                this.handleMessage(payload, consumerMessageCopy, messageExt);
            } catch (Throwable e) {
                // 记录到数据库中的异常会因为重新消费被清除，在日志中也记录一下
                LOGGER.error("consume message error, id: {}", message.getId(), e);
                try {
                    boolean markFailureResult = messageService.markFailure(message);
                    /*
                     * 一般在消费超时时会出现这种情况，应该设置更合理的超时时间
                     * 如果有其他线程更新了消息，状态或重试次数发生了变化，更新会失败
                     * 状态变化：
                     *     1. 变为成功：已经成功了，就不用再更新成失败了
                     *     2. 变为失败：（不考虑前后两种错误不一致的情况），已经有其他消费线程标记为失败，并记录了错误
                     * 重试次数变化：
                     *     1. 其他消费线程正在消费（状态为消费中）
                     *     2. 其他消费线程已经消费了（状态为成功或失败，同状态变化）
                     */
                    if (markFailureResult) {
                        messageService.addErrorLog(message, e);
                    }
                } catch (Throwable ex) {
                    LOGGER.error("exception occurred while mark message as failure, id: {}", message.getId(), ex);
                }
                throw e;
            }
            boolean markSuccessResult = messageService.markSuccess(message);
            if (!markSuccessResult) {
                // 消费逻辑执行完了，但是更新状态失败了，记录日志，不能抛异常，否则会重新消费
                LOGGER.error("exception occurred while mark message as success, id: {}", message.getId());
            }
            messageService.cleanErrorLog(message);
            return;
        }
        // 如果不可消费，会刷新message对象的属性，可能的状态有两种：消费完成、正在消费还未超时，按照状态处理
        switch (message.getStatus()) {
            case P0:
                /*
                 * 正在消费，需要异常退出，rocketmq-spring-boot-start做了处理：
                 * 1. 顺序消息，返回SUSPEND_CURRENT_QUEUE_A_MOMENT，当前队列等待一段时间后重新发送，不会影响当前队列消息的顺序
                 * 2. 并发消息，返回RECONSUME_LATER，当前消息稍后重发，继续消费后续的消息
                 * 所以，当消息状态是正在消费，不能保证一定会消费成功，需要重新发送
                 */
                throw new XkwRocketmqConsumerException(String.format("retry later (message id: %s)", message.getId()));
            case P1:
                /*
                 * 兜个底
                 * 消息状态为失败，但是不可消费，这种情况不应该发生
                 * 但是如果真的发生了，就是组件的bug，抛出异常，等待重新消费
                 */
                throw new XkwRocketmqConsumerException(String.format("component bug, retry later (message id: %s)", message.getId()));
        }
        // 消费已经完成，正常退出方法
    }
}
