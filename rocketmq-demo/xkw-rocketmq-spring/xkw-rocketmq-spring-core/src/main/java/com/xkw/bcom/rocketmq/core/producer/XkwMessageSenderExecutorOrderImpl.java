/*
 * xkw.com Inc. Copyright (c) 2023 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.producer;

import com.xkw.bcom.rocketmq.core.component.XkwRocketmqThreadFactoryImpl;
import com.xkw.bcom.rocketmq.core.message.XkwMessageType;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessage;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessageStatus;
import com.xkw.bcom.rocketmq.core.repository.XkwRocketmqProducerRepository;
import com.xkw.bcom.rocketmq.core.sender.impl.XkwDelegatorMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * XkwMessageSenderExecutorOrderImpl
 * <p>顺序消息比较特殊，如果使用普通的异步发送{@link XkwMessageSenderExecutorDefaultImpl}，在并发较高的时候可能会导致消息顺序错乱
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2023年05月06日
 */
public class XkwMessageSenderExecutorOrderImpl implements XkwMessageSenderExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(XkwMessageSenderExecutorOrderImpl.class);
    private static final String THREAD_NAME_PREFIX_FORMAT = "xkw-rocketmq-order-sender-executor-thread-%s-";

    private int threadPoolCount;
    private int queueSize;
    private ThreadPoolExecutor[] threadPoolExecutors;
    private int maxRetryTimes;

    @Resource
    private XkwDelegatorMessageSender delegatorMessageSender;
    @Resource
    private XkwRocketmqProducerRepository producerRepository;

    @PostConstruct
    public void init() {
        this.threadPoolExecutors = new ThreadPoolExecutor[this.threadPoolCount];
        for (int i = 0; i < this.threadPoolCount; i++) {
            String namePrefix = String.format(THREAD_NAME_PREFIX_FORMAT, i);
            ThreadFactory threadFactory = new XkwRocketmqThreadFactoryImpl(namePrefix);
            threadPoolExecutors[i] = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(queueSize), threadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());
        }
    }

    public void execute(List<XkwProducerMessage> messages) {
        for (XkwProducerMessage message : messages) {
            SendMessageTask task = new SendMessageTask(message.getId());
            if (this.threadPoolCount == 1) {
                this.threadPoolExecutors[0].execute(task);
                continue;
            }
            String hashKey = message.getHashKey();
            int hash = hashKey.hashCode() & Integer.MAX_VALUE;
            int index = hash % this.threadPoolCount;
            this.threadPoolExecutors[index].execute(task);
        }
    }

    public void setThreadPoolCount(int threadPoolCount) {
        this.threadPoolCount = threadPoolCount;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public void setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    class SendMessageTask implements Runnable {

        private final Long id;

        public SendMessageTask(Long id) {
            this.id = id;
        }

        @Override
        public void run() {
            List<XkwProducerMessage> messages = producerRepository.listByIds(Collections.singletonList(id));
            if (messages == null || messages.isEmpty()) {
                return;
            }
            XkwProducerMessage message = messages.get(0);
            // 检查一下消息状态，防止因为线程等待时间过长，消息已经被轮询任务发送过
            if (message.getStatus() != XkwProducerMessageStatus.P0) {
                return;
            }
            // 根据hashKey查询第一个待发送的消息，如果不是当前消息，那说明更早的消息还未发送
            XkwProducerMessage firstUnsentMessageWithSameHashKey = producerRepository
                    .findFirstUnsentMessageByHashKey(message.getNamespace(), XkwMessageType.ORDER, message.getHashKey(), maxRetryTimes);
            if (firstUnsentMessageWithSameHashKey == null) {
                // 没有查询到，根据当前消息的hashKey没有查询到消息，表示当前消息也已经被其他线程发送过了
                return;
            }
            if (!Objects.equals(firstUnsentMessageWithSameHashKey.getId(), message.getId())) {
                // 查询到但是该消息不是当前消息，表示有更早的未发送的消息，当前消息暂不发送
                return;
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("send message by order sender, messageId: {}", message.getId());
            }
            delegatorMessageSender.send(Collections.singletonList(message));
        }
    }
}
