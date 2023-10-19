/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.autoconfigure.config;

import com.xkw.bcom.rocketmq.autoconfigure.condition.ConditionalOnPropertyGreaterThan;
import com.xkw.bcom.rocketmq.autoconfigure.properties.XkwRocketmqProperties;
import com.xkw.bcom.rocketmq.core.constant.XkwRocketmqConstant;
import com.xkw.bcom.rocketmq.core.producer.XkwMessageSender;
import com.xkw.bcom.rocketmq.core.producer.XkwMessageSenderExecutorDefaultImpl;
import com.xkw.bcom.rocketmq.core.producer.XkwMessageSenderExecutorOrderImpl;
import com.xkw.bcom.rocketmq.core.repository.XkwRocketmqLockRepository;
import com.xkw.bcom.rocketmq.core.repository.XkwRocketmqProducerRepository;
import com.xkw.bcom.rocketmq.core.repository.mysql.XkwRocketmqLockRepositoryMysqlImpl;
import com.xkw.bcom.rocketmq.core.repository.mysql.XkwRocketmqProducerRepositoryMysqlImpl;
import com.xkw.bcom.rocketmq.core.sender.impl.XkwConcurrentMessageSender;
import com.xkw.bcom.rocketmq.core.sender.impl.XkwDelayMessageSender;
import com.xkw.bcom.rocketmq.core.sender.impl.XkwDelegatorMessageSender;
import com.xkw.bcom.rocketmq.core.sender.impl.XkwOrderMessageSender;
import com.xkw.bcom.rocketmq.core.service.XkwRocketmqProducerService;
import com.xkw.bcom.rocketmq.core.service.impl.XkwRocketmqProducerServiceImpl;
import com.xkw.bcom.rocketmq.core.task.XkwRocketmqProducerMessageCleanTask;
import com.xkw.bcom.rocketmq.core.task.XkwRocketmqProducerMessageScanTask;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * XkwRocketmqProducerAutoConfiguration
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月11日
 */
@Configuration
@ConditionalOnProperty(
        name = "xkw-rocketmq.producer.enable",
        havingValue = "true",
        matchIfMissing = true
)
@ConditionalOnBean({RocketMQTemplate.class, RocketMQProperties.class})
@AutoConfigureAfter(RocketMQAutoConfiguration.class)
@EnableConfigurationProperties(XkwRocketmqProperties.class)
@EnableScheduling
@Import(XkwRocketmqCommonConfiguration.class)
public class XkwRocketmqProducerAutoConfiguration {

    @Bean
    public XkwMessageSender xkwMessageSender(XkwRocketmqProperties xkwRocketmqProperties,
                                             RocketMQProperties rocketMQProperties) {
        XkwMessageSender sender = new XkwMessageSender();
        String keyPrefix = xkwRocketmqProperties.getProducer().getKeyPrefix();
        if (keyPrefix == null || keyPrefix.equals(XkwRocketmqConstant.BLANK)) {
            keyPrefix = rocketMQProperties.getProducer().getGroup() + "-";
        }
        String namespace = rocketMQProperties.getProducer().getNamespace();
        sender.setKeyPrefix(keyPrefix);
        sender.setNamespace(namespace);
        return sender;
    }

    @Bean
    public XkwMessageSenderExecutorDefaultImpl defaultMessageSenderExecutor(XkwRocketmqProperties xkwRocketmqProperties) {
        XkwMessageSenderExecutorDefaultImpl executor = new XkwMessageSenderExecutorDefaultImpl();
        XkwRocketmqProperties.ThreadPool threadPool = xkwRocketmqProperties.getProducer().getThreadPool();
        executor.setCorePoolSize(threadPool.getCorePoolSize());
        executor.setMaximumPoolSize(threadPool.getMaximumPoolSize());
        executor.setKeepAliveTime(threadPool.getKeepAliveTime());
        executor.setQueueSize(threadPool.getQueueSize());
        return executor;
    }

    @Bean
    public XkwMessageSenderExecutorOrderImpl orderMessageSenderExecutor(XkwRocketmqProperties xkwRocketmqProperties) {
        XkwMessageSenderExecutorOrderImpl executor = new XkwMessageSenderExecutorOrderImpl();
        XkwRocketmqProperties.Producer producer = xkwRocketmqProperties.getProducer();
        XkwRocketmqProperties.ThreadPool threadPool = producer.getThreadPool();
        int threadPoolCount = threadPool.getCorePoolSize();
        if (threadPoolCount >= 2) {
            threadPoolCount = threadPoolCount >> 1;
        }
        executor.setThreadPoolCount(threadPoolCount);
        executor.setQueueSize(threadPool.getQueueSize());
        executor.setMaxRetryTimes(producer.getMaxRetryTimes());
        return executor;
    }

    @Bean
    public TransactionDefinition transactionDefinition() {
        return new DefaultTransactionDefinition();
    }

    @Bean
    public XkwDelegatorMessageSender xkwDelegatorMessageSender() {
        return new XkwDelegatorMessageSender();
    }

    @Bean
    public XkwConcurrentMessageSender xkwConcurrentMessageSender() {
        return new XkwConcurrentMessageSender();
    }

    @Bean
    public XkwOrderMessageSender xkwOrderMessageSender() {
        return new XkwOrderMessageSender();
    }

    @Bean
    public XkwDelayMessageSender xkwDelayMessageSender() {
        return new XkwDelayMessageSender();
    }

    @Bean
    @ConditionalOnPropertyGreaterThan("xkw-rocketmq.producer.message-overdue-days")
    public XkwRocketmqProducerMessageCleanTask xkwRocketmqProducerMessageCleanTask(XkwRocketmqProperties xkwRocketmqProperties,
                                                                                   RocketMQProperties rocketMQProperties) {
        XkwRocketmqProperties.Producer producer = xkwRocketmqProperties.getProducer();
        int messageOverdueDays = producer.getMessageOverdueDays();
        long cleanOverdueMessageExpireMillis = producer.getCleanOverdueMessageExpireMillis();
        if (cleanOverdueMessageExpireMillis < 1000) {
            cleanOverdueMessageExpireMillis = 1000;
        }
        XkwRocketmqProducerMessageCleanTask task = new XkwRocketmqProducerMessageCleanTask();
        String namespace = rocketMQProperties.getProducer().getNamespace();
        if (StringUtils.isBlank(namespace)) {
            task.setLockId("1");
        } else {
            task.setLockId(namespace + "-1");
        }
        task.setLockExpireMillis(cleanOverdueMessageExpireMillis);
        task.setMessageOverdueDays(messageOverdueDays);
        return task;
    }

    @Bean
    public XkwRocketmqProducerMessageScanTask xkwRocketmqProducerMessageScanTask(XkwRocketmqProperties xkwRocketmqProperties,
                                                                                 RocketMQProperties rocketMQProperties) {
        XkwRocketmqProperties.Producer producer = xkwRocketmqProperties.getProducer();
        int maxRetryTimes = producer.getMaxRetryTimes();
        if (maxRetryTimes < 0) {
            maxRetryTimes = 0;
        }
        long scanMessageExpireMillis = producer.getScanMessageExpireMillis();
        if (scanMessageExpireMillis < 1000) {
            scanMessageExpireMillis = 1000;
        }
        XkwRocketmqProducerMessageScanTask task = new XkwRocketmqProducerMessageScanTask();
        String namespace = rocketMQProperties.getProducer().getNamespace();
        if (StringUtils.isBlank(namespace)) {
            task.setLockId("2");
        } else {
            task.setLockId(namespace + "-2");
        }
        task.setLockExpireMillis(scanMessageExpireMillis);
        task.setMaxRetryTimes(maxRetryTimes);
        task.setStartDelay(xkwRocketmqProperties.getProducer().getScanStartDelay());
        return task;
    }

    @Bean
    public XkwRocketmqProducerService xkwRocketmqProducerService(XkwRocketmqProperties xkwRocketmqProperties,
                                                                 RocketMQProperties rocketMQProperties) {
        XkwRocketmqProducerServiceImpl producerService = new XkwRocketmqProducerServiceImpl();
        producerService.setNamespace(rocketMQProperties.getProducer().getNamespace());
        producerService.setMaxRetryTimes(xkwRocketmqProperties.getProducer().getMaxRetryTimes());
        return producerService;
    }

    @Configuration
    @ConditionalOnProperty(
            name = "xkw-rocketmq.producer.data-source-type",
            havingValue = "mysql_innodb",
            matchIfMissing = true
    )
    public static class MysqlInnoDBConfiguration {

        @Bean
        public XkwRocketmqProducerRepository xkwRocketmqProducerRepository() {
            return new XkwRocketmqProducerRepositoryMysqlImpl();
        }

        @Bean
        @ConditionalOnMissingBean
        public XkwRocketmqLockRepository xkwRocketmqLockRepository() {
            return new XkwRocketmqLockRepositoryMysqlImpl();
        }
    }
}
