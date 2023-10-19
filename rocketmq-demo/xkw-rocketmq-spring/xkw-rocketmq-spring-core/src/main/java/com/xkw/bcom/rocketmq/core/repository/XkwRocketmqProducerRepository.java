/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.repository;

import com.xkw.bcom.rocketmq.core.message.XkwMessageType;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessage;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessageStatus;

import java.util.List;

/**
 * XkwRocketmqProducerRepository
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月11日
 */
public interface XkwRocketmqProducerRepository {

    /**
     * 插入消息
     *
     * @param message 生产者消息
     * @return 消息id
     */
    Long save(XkwProducerMessage message);

    /**
     * 批量插入消息
     *
     * @param messages 生产者消息集合
     * @return 消息id集合
     */
    List<Long> saveBatch(List<XkwProducerMessage> messages);

    /**
     * 根据id批量查询
     *
     * @param ids 消息id集合
     * @return XkwProducerMessage集合
     */
    List<XkwProducerMessage> listByIds(List<Long> ids);

    /**
     * 根据id批量更新消息状态
     *
     * @param ids 消息id集合
     * @param status 消息状态
     */
    void updateStatusByIds(List<Long> ids, XkwProducerMessageStatus status);

    /**
     * 根据id批量更新消息状态和提交时间
     *
     * @param ids 消息id集合
     * @param status 消息状态
     * @param commitTime 提交时间
     */
    void updateStatusAndCommitTimeByIds(List<Long> ids, XkwProducerMessageStatus status, long commitTime);

    /**
     * 根据id批量更新消息状态并增加重试次数
     *
     * @param ids 消息id集合
     * @param status 消息状态
     */
    void updateStatusAndIncreaseRetryTimesByIds(List<Long> ids, XkwProducerMessageStatus status);

    /**
     * 根据消息状态和创建时间滚动查询消息id
     *
     * @param namespace 名称空间
     * @param status 消息状态
     * @param maxCreateTime 最大创建时间
     * @param minId 最小id
     * @param limit limit
     * @return 消息id集合
     */
    List<Long> listIdsByStatusAndCreateTime(String namespace, XkwProducerMessageStatus status, long maxCreateTime, long minId, int limit);

    /**
     * 根据id删除消息
     *
     * @param ids 消息id集合
     */
    void deleteByIds(List<Long> ids);

    /**
     * 查询未发送的消息，status = P1 OR (status = P0 AND createTime < maxCreateTime)
     *
     * @param namespace 名称空间
     * @param maxCreateTime 最大创建时间
     * @param minId 最小id
     * @param maxRetryTimes 最大重试次数
     * @param limit limit
     * @return 消息集合
     */
    List<XkwProducerMessage> listUnsentMessages(String namespace, long maxCreateTime, long minId, int maxRetryTimes, int limit);

    /**
     * 根据hashKey查询最早的未发送的消息
     *
     * @param namespace 名称空间
     * @param type 消息类型
     * @param hashKey hashKey
     * @param maxRetryTimes 最大重试次数
     * @return 生产者消息
     */
    XkwProducerMessage findFirstUnsentMessageByHashKey(String namespace, XkwMessageType type, String hashKey, int maxRetryTimes);

    /**
     * 根据最小id、消息状态、重试次数查询
     *
     * @param namespace 名称空间
     * @param minId 最小id，不包含
     * @param status 消息状态
     * @param maxRetryTimes 最大重试次数
     * @param limit limit
     * @return 生产者消息集合
     */
    List<XkwProducerMessage> listByMinIdAndStatusAndRetryTimes(String namespace, long minId, XkwProducerMessageStatus status, int maxRetryTimes, int limit);
}
