/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.service;

import com.xkw.bcom.rocketmq.core.message.XkwConsumerErrorMessageVO;

import java.util.List;

/**
 * XkwRocketmqConsumerService
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月25日
 */
public interface XkwRocketmqConsumerService {

    /**
     * 根据uniqueKey查询消费失败的消息
     *
     * @param uniqueKey 消息key
     * @return XkwConsumerErrorMessageVO
     */
    XkwConsumerErrorMessageVO getUnfinishedMessageByUniqueKey(String uniqueKey);

    /**
     * 查询消费失败的消息
     *
     * @param minId 最小id，不包含
     * @param topic 主题
     * @param tag tag
     * @param minRetryTimes 最小重试次数
     * @param minStartTime 最小开始时间
     * @param limit limit
     * @return 异常消息集合
     */
    List<XkwConsumerErrorMessageVO> listUnfinishedMessages(long minId, String topic, String tag, Integer minRetryTimes,
                                                           Long minStartTime, int limit);

    /**
     * 查询消费失败的消息数量
     *
     * @param topic 主题
     * @param tag tag
     * @param minRetryTimes 最小重试次数
     * @param minStartTime 最小开始时间
     * @return 消费失败的消息数量
     */
    int countUnfinishedMessages(String topic, String tag, Integer minRetryTimes, Long minStartTime);

    /**
     * 根据id删除消息
     *
     * @param ids 消息id集合
     */
    void deleteMessages(List<Long> ids);
}
