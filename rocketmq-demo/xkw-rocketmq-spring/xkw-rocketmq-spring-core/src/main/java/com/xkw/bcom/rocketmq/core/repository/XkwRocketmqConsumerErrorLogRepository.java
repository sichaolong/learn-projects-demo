/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.repository;

import com.xkw.bcom.rocketmq.core.message.XkwConsumerErrorLog;

import java.util.List;

/**
 * XkwRocketmqConsumerErrorLogRepository
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月28日
 */
public interface XkwRocketmqConsumerErrorLogRepository {

    Long save(XkwConsumerErrorLog errorLog);

    void deleteByIds(List<Long> ids);

    List<Long> listIdsByMessageId(long messageId);

    List<Long> listIdsByMessageIds(List<Long> messageIds);

    List<XkwConsumerErrorLog> listByMessageIds(List<Long> messageIds);
}
