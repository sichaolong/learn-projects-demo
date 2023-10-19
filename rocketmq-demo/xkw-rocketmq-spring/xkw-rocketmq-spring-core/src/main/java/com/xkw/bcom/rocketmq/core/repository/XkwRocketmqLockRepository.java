/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.repository;

/**
 * XkwRocketmqLockRepository
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月21日
 */
public interface XkwRocketmqLockRepository {

    /**
     * 获取分布式锁
     *
     * @param id 锁id
     * @param expireMillis 锁超时时间
     * @return boolean
     */
    boolean acquire(String id, long expireMillis);

    /**
     * 释放分布式锁
     *
     * @param id 锁id
     * @param expireMillis 锁超时时间
     * @return boolean
     */
    boolean release(String id, long expireMillis);

    /**
     * 刷新分布式锁的时间
     *
     * @param id 锁id
     * @param expireMillis 锁超时时间
     * @return boolean
     */
    boolean keepalive(String id, long expireMillis);
}
