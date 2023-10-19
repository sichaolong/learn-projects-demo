/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.service;

import com.xkw.bcom.rocketmq.core.message.XkwProducerMessage;

import java.util.List;

/**
 * XkwRocketmqProducerService
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月25日
 */
public interface XkwRocketmqProducerService {

    /**
     * 查询发送失败且不再重试的消息
     *
     * @param minId 最小id，不包含
     * @param limit limit
     * @return 生产者消息集合
     */
    List<XkwProducerMessage> listFailedMessages(long minId, int limit);

    /**
     * 根据消息id，重新发送消息到mq
     * <p>只重发到mq，不插入新的记录
     *
     * @param ids 消息id集合
     */
    void resendMessages(List<Long> ids);
}
