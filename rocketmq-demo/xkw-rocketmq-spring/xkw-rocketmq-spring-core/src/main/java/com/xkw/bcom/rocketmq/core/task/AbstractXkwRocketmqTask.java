/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.task;

import com.xkw.bcom.rocketmq.core.repository.XkwRocketmqLockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * AbstractXkwRocketmqTask
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月21日
 */
public abstract class AbstractXkwRocketmqTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractXkwRocketmqTask.class);

    @Resource
    private XkwRocketmqLockRepository lockRepository;

    private String lockId;
    private long lockExpireMillis;
    /**
     * 埋下一个钩子，当watchdog续命失败时可以中断任务执行
     */
    private volatile boolean status = false;

    private final ScheduledExecutorService watchdogExecutor = new ScheduledThreadPoolExecutor(1);

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    public void setLockExpireMillis(long lockExpireMillis) {
        this.lockExpireMillis = lockExpireMillis;
    }

    protected boolean getStatus() {
        return this.status;
    }

    public void action() {
        // 获取分布式锁
        boolean acquireResult = lockRepository.acquire(lockId, lockExpireMillis);
        if (!acquireResult) {
            return;
        }
        // 开启watchdog，防止处理超时
        long period = lockExpireMillis >> 1;
        ScheduledFuture<?> future = watchdogExecutor.scheduleAtFixedRate(new Watchdog(), period, period, TimeUnit.MILLISECONDS);

        this.status = true;
        this.mainAction();
        this.status = false;

        // 关闭watchdog
        future.cancel(true);
        try {
            // 等待watchdog结束
            future.get();
        } catch (Exception ignore) {
            // cancel之后get肯定会出现异常
        }

        // 释放分布式锁
        boolean releaseResult = lockRepository.release(lockId, lockExpireMillis);
        if (!releaseResult) {
            LOGGER.warn("lock release result false, lockId: {}", lockId);
        }
    }

    abstract void mainAction();

    class Watchdog implements Runnable {

        @Override
        public void run() {
            boolean result;
            try {
                result = lockRepository.keepalive(lockId, lockExpireMillis);
            } catch (Throwable e) {
                result = false;
                String message = String.format("lock keepalive failure, lockId: %s, lockExpireMillis: %s", lockId, lockExpireMillis);
                LOGGER.error(message, e);
            }
            if (!result) {
                status = false;
            }
        }
    }
}
