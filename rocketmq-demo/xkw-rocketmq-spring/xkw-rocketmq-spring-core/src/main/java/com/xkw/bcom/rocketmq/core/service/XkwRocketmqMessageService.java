/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.service;

import com.xkw.bcom.rocketmq.core.message.XkwConsumerMessage;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * XkwRocketmqMessageService
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月24日
 */
public interface XkwRocketmqMessageService {

    /**
     * 转换消费者消息对象
     *
     * @param messageExt MessageExt
     * @param namespace 名称空间
     * @return 消费者消息
     */
    XkwConsumerMessage parseMessage(MessageExt messageExt, String namespace);

    /**
     * 记录异常日志
     *
     * @param message 消费者消息
     * @param throwable 异常
     */
    void addErrorLog(XkwConsumerMessage message, Throwable throwable);

    /**
     * 清理异常消息日志
     *
     * @param message 消费者消息
     */
    void cleanErrorLog(XkwConsumerMessage message);

    /**
     * 检查幂等消息是否可以被消费
     * <p>1. 从数据查询消息
     * <p>1.1 消息在表中不存在，插入消息，状态为消费中，开始时间为当前时间
     * <p>1.1.1 插入成功，返回true
     * <p>1.1.2 插入失败，[不可消费]
     * <p>1.2 消息在表中存在，状态为失败，更新消息状态为消费中，开始时间为当前时间，重试次数+1
     * <p>1.2.1 更新成功，返回true
     * <p>1.2.2 更新失败，[不可消费]
     * <p>1.3 消息在表中存在，状态为消费中，开始时间 <= (当前时间 - expireMillis)，更新消息状态为消费中，开始时间为当前时间，重试次数+1
     * <p>1.3.1 更新成功，返回true
     * <p>1.3.2 更新失败，[不可消费]
     * <p>1.4 消息在表中存在，状态为消费中，开始时间 > (当前时间 - expireMillis)，[不可消费]
     * <p>1.5 消息在表中存在，状态为成功，[不可消费]
     *
     * <p>不可消费：重新从数据库查询消息，更新message对象的属性，返回false
     *
     * @param message 消费者消息
     * @param consumeTimeout 消费超时时间
     * @return boolean
     */
    boolean shouldConsume(XkwConsumerMessage message, long consumeTimeout);

    /**
     * 更新消息状态为失败
     *
     * @param message 消息
     * @return boolean
     */
    boolean markFailure(XkwConsumerMessage message);

    /**
     * 更新消息状态为成功，结束时间为当前时间
     *
     * @param message 消息
     * @return boolean
     */
    boolean markSuccess(XkwConsumerMessage message);
}
