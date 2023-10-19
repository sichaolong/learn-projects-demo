/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.sender;

import com.xkw.bcom.rocketmq.core.message.XkwProducerMessage;

import java.util.List;

/**
 * XkwMessageSenderStrategy
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月17日
 */
public interface XkwMessageSenderStrategy {

    /**
     * 批量发送消息
     * <p>若发送成功，将消息状态更新为成功，并更新发送时间
     * <p>否则更新消息状态为失败，等待轮训
     *
     * @param producerMessages 生产者消息集合
     * @return true-继续发送，false-等一等再发
     */
    boolean send(List<XkwProducerMessage> producerMessages);
}
