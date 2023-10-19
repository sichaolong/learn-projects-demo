/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.producer;

import com.xkw.bcom.rocketmq.core.component.XkwRocketmqThreadFactoryImpl;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessage;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessageStatus;
import com.xkw.bcom.rocketmq.core.repository.XkwRocketmqProducerRepository;
import com.xkw.bcom.rocketmq.core.sender.impl.XkwDelegatorMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * XkwMessageSenderExecutorDefaultImpl
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月25日
 */
public class XkwMessageSenderExecutorDefaultImpl implements XkwMessageSenderExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(XkwMessageSenderExecutorDefaultImpl.class);
    private static final String THREAD_NAME_PREFIX = "xkw-rocketmq-default-sender-executor-thread-";

    private int corePoolSize;
    private int maximumPoolSize;
    private long keepAliveTime;
    private int queueSize;
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private XkwDelegatorMessageSender delegatorMessageSender;
    @Resource
    private XkwRocketmqProducerRepository producerRepository;

    @PostConstruct
    public void init() {
        /*
         * 临界时间1分钟，超过这个时间未发送的消息会被轮训到
         * 所以拒绝策略选择默默丢弃最早的任务
         */
        ThreadFactory threadFactory = new XkwRocketmqThreadFactoryImpl(THREAD_NAME_PREFIX);
        threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(queueSize), threadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    public void execute(List<XkwProducerMessage> messages) {
        List<Long> ids = messages.stream()
                .map(XkwProducerMessage::getId)
                .collect(Collectors.toList());
        SendMessageTask task = new SendMessageTask(ids);
        this.threadPoolExecutor.execute(task);
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    class SendMessageTask implements Runnable {

        private final List<Long> ids;

        public SendMessageTask(List<Long> ids) {
            this.ids = ids;
        }

        @Override
        public void run() {
            List<XkwProducerMessage> messages = producerRepository.listByIds(ids);
            // 需要过滤一下，防止因为线程等待时间过长，消息已经被轮询任务发送过，不过滤可能会重复发送
            messages = messages.stream()
                    .filter(message -> message.getStatus() == XkwProducerMessageStatus.P0)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(messages)) {
                return;
            }
            if (LOGGER.isDebugEnabled()) {
                String messageIdsJoin = messages.stream()
                        .map(XkwProducerMessage::getId)
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));
                LOGGER.debug("send message by default sender, messageIds: {}", messageIdsJoin);
            }
            delegatorMessageSender.send(messages);
        }
    }
}
