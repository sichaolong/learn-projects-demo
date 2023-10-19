/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.repository;

import com.xkw.bcom.rocketmq.core.message.XkwConsumerMessage;
import com.xkw.bcom.rocketmq.core.message.XkwConsumerMessageStatus;

import java.util.List;

/**
 * XkwRocketmqConsumerRepository
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月11日
 */
public interface XkwRocketmqConsumerRepository {

    /**
     * 插入消息
     *
     * @param message 消息
     * @return 消费者消息id
     */
    Long save(XkwConsumerMessage message);

    /**
     * 如果消费未成功，将消息标记为消费中
     * <p>根据id、重试次数，更新未成功的消息状态状态为消费中，刷新开始时间，累加重试次数
     *
     * @param message 消息
     * @param condition 更新条件
     * @return boolean
     */
    boolean markPendingIfNotSuccess(XkwConsumerMessage message, XkwConsumerMessage condition);

    /**
     * 将消息标记为消费失败
     * <p>根据id、状态、重试次数，更新消息状态
     *
     * @param message 消息
     * @param condition 更新条件
     * @return boolean
     */
    boolean markFailure(XkwConsumerMessage message, XkwConsumerMessage condition);

    /**
     * 将消息标记为消费成功
     * <p>根据id，更新消息状态、开始时间、结束时间
     *
     * @param message 消息
     * @param condition 更新条件
     * @return boolean
     */
    boolean markSuccess(XkwConsumerMessage message, XkwConsumerMessage condition);

    /**
     * 根据key查询消费者消息
     * <p>这里查询条件不需要包含namespace，因为生产者在生成key时已经拼上了namespace
     *
     * @param key key
     * @return 消费者消息
     */
    XkwConsumerMessage findByKey(String key);

    /**
     * 根据消息状态和开始时间滚动查询消息id
     *
     * @param namespace 名称空间
     * @param status 消息状态
     * @param maxStartTime 最大开始时间
     * @param minId 最小id
     * @param limit limit
     * @return 消息id集合
     */
    List<Long> listIdsByStatusAndStartTime(String namespace, XkwConsumerMessageStatus status, long maxStartTime, long minId, int limit);

    /**
     * 根据id删除消息
     *
     * @param ids 消息id集合
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据最小id和消息状态查询
     *
     * @param namespace 名称空间
     * @param minId 最小id，不包含
     * @param topic 主题
     * @param tag tag
     * @param minRetryTimes 最小重试次数
     * @param minStartTime 最小开始时间
     * @param status 消息状态
     * @param limit limit
     * @return 消费者消息集合
     */
    List<XkwConsumerMessage> listByConditions(String namespace, long minId, String topic, String tag, Integer minRetryTimes,
                                              Long minStartTime, XkwConsumerMessageStatus status, int limit);

    /**
     * 查询消费失败的消息数量
     *
     * @param namespace 名称空间
     * @param topic 主题
     * @param tag tag
     * @param minRetryTimes 最小重试次数
     * @param minStartTime 最小开始时间
     * @param status 消息状态
     * @return 消费失败的消息数量
     */
    int countByConditions(String namespace, String topic, String tag, Integer minRetryTimes,
                          Long minStartTime, XkwConsumerMessageStatus status);
}
