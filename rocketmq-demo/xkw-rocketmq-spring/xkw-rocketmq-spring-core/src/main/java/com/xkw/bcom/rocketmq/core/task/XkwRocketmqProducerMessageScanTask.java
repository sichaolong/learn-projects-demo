/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.task;

import com.xkw.bcom.rocketmq.core.constant.XkwRocketmqConstant;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessage;
import com.xkw.bcom.rocketmq.core.repository.XkwRocketmqProducerRepository;
import com.xkw.bcom.rocketmq.core.sender.impl.XkwDelegatorMessageSender;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * XkwRocketmqProducerMessageScanTask
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月20日
 */
public class XkwRocketmqProducerMessageScanTask extends AbstractXkwRocketmqTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(XkwRocketmqProducerMessageScanTask.class);
    private static final int LIMIT = XkwRocketmqConstant.BATCH_SIZE * 5;
    /**
     * 因为提交事务时会自动发送消息，扫描数据库的间隔可以长一些，避免无效的轮询，降低数据库压力
     */
    private static final long MAX_INTERVAL = 60 * 1000L;
    private static final long MIN_INTERVAL = 10 * 1000L;

    @Resource
    private XkwRocketmqProducerRepository producerRepository;
    @Resource
    private XkwDelegatorMessageSender delegatorMessageSender;
    @Resource
    private RocketMQProperties rocketMQProperties;

    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private int maxRetryTimes;
    private int startDelay;

    public void setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    public void setStartDelay(int startDelay) {
        this.startDelay = startDelay;
    }

    @PostConstruct
    public void init() {
        Thread thread = new Thread(this, "xkw-producer-message-scan-task");
        thread.start();
    }

    @Override
    void mainAction() {
        long scanInterval = 0L;
        String namespace = rocketMQProperties.getProducer().getNamespace();
        while (getStatus()) {
            long maxCreateTime = System.currentTimeMillis() - 60 * 1000L;
            List<XkwProducerMessage> messages = producerRepository.listUnsentMessages(namespace, maxCreateTime, 0,
                    maxRetryTimes, LIMIT);
            if (CollectionUtils.isEmpty(messages)) {
                // 轮询不到消息就间隔一段时间
                if (scanInterval < MAX_INTERVAL) {
                    scanInterval += MIN_INTERVAL;
                }
                safeSleep(scanInterval);
                continue;
            }
            scanInterval = 0L;
            if (!getStatus()) {
                // 分布式锁丢了，结束方法，防止重复发送消息
                return;
            }
            if (LOGGER.isDebugEnabled()) {
                String messageIdsJoin = messages.stream()
                        .map(XkwProducerMessage::getId)
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));
                LOGGER.debug("send message by scan task, messageIds: {}", messageIdsJoin);
            }
            /*
             * 已知的异常已经在send方法中处理了
             * 不捕获未知异常，异常退出即可，当前节点会释放分布式锁，其他节点有机会获得锁，避免当前节点故障导致发送失败
             */
            if (!delegatorMessageSender.send(messages)) {
                // send返回false，需要等一等
                safeSleep(MAX_INTERVAL);
            }
        }
    }

    @Override
    public void run() {
        if (startDelay > 0) {
            safeSleep(this.startDelay * 1000L);
        }
        while (true) {
            try {
                lock.lock();
                this.action();
                /*
                 * action方法结束表示当前节点没有获取到分布式锁
                 * 等待定时任务唤醒进入下一轮循环
                 */
                condition.await();
            } catch (Throwable e) {
                // 不能抛异常，否则线程会中断
                LOGGER.error("scan message error", e);
                // 出现异常的原因可能有数据库连接问题，消息表没有创建等，这里最好等待一下，否则可能产生大量异常日志
                safeSleep(MAX_INTERVAL);
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * 每分钟唤醒一次任务线程
     * 当执行任务的节点宕机时可以自动切换到存活的节点
     */
    @Scheduled(cron = "0 * * * * *")
    public void wakeup() {
        if (!lock.tryLock()) {
            // 如果没有获取到锁，说明任务已经在执行了
            return;
        }
        try {
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    private void safeSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignore) {
        }
    }
}
