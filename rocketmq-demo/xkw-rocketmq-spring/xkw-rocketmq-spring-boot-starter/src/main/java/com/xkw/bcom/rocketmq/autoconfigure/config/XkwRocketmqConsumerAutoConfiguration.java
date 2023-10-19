/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.autoconfigure.config;

import com.xkw.bcom.rocketmq.autoconfigure.condition.ConditionalOnPropertyGreaterThan;
import com.xkw.bcom.rocketmq.autoconfigure.properties.XkwRocketmqProperties;
import com.xkw.bcom.rocketmq.core.repository.XkwRocketmqConsumerErrorLogRepository;
import com.xkw.bcom.rocketmq.core.repository.XkwRocketmqConsumerRepository;
import com.xkw.bcom.rocketmq.core.repository.XkwRocketmqLockRepository;
import com.xkw.bcom.rocketmq.core.repository.mysql.XkwRocketmqConsumerErrorLogRepositoryMysqlImpl;
import com.xkw.bcom.rocketmq.core.repository.mysql.XkwRocketmqConsumerRepositoryMysqlImpl;
import com.xkw.bcom.rocketmq.core.repository.mysql.XkwRocketmqLockRepositoryMysqlImpl;
import com.xkw.bcom.rocketmq.core.service.XkwRocketmqConsumerService;
import com.xkw.bcom.rocketmq.core.service.XkwRocketmqMessageService;
import com.xkw.bcom.rocketmq.core.service.impl.XkwRocketmqConsumerServiceImpl;
import com.xkw.bcom.rocketmq.core.service.impl.XkwRocketmqMessageServiceImpl;
import com.xkw.bcom.rocketmq.core.task.XkwRocketmqConsumerMessageCleanTask;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * XkwRocketmqConsumerAutoConfiguration
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月11日
 */
@Configuration
@ConditionalOnProperty(
        name = "xkw-rocketmq.consumer.enable",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(XkwRocketmqProperties.class)
@EnableScheduling
@Import(XkwRocketmqCommonConfiguration.class)
public class XkwRocketmqConsumerAutoConfiguration {

    @Bean
    @ConditionalOnPropertyGreaterThan("xkw-rocketmq.consumer.message-overdue-days")
    public XkwRocketmqConsumerMessageCleanTask xkwRocketmqConsumerMessageCleanTask(XkwRocketmqProperties xkwRocketmqProperties,
                                                                                   RocketMQProperties rocketMQProperties) {
        XkwRocketmqProperties.Consumer consumer = xkwRocketmqProperties.getConsumer();
        int messageOverdueDays = consumer.getMessageOverdueDays();
        long cleanOverdueMessageExpireMillis = consumer.getCleanOverdueMessageExpireMillis();
        if (cleanOverdueMessageExpireMillis < 1000) {
            cleanOverdueMessageExpireMillis = 1000;
        }
        XkwRocketmqConsumerMessageCleanTask task = new XkwRocketmqConsumerMessageCleanTask();
        String namespace = rocketMQProperties.getConsumer().getNamespace();
        if (StringUtils.isBlank(namespace)) {
            task.setLockId("3");
        } else {
            task.setLockId(namespace + "-3");
        }
        task.setLockExpireMillis(cleanOverdueMessageExpireMillis);
        task.setMessageOverdueDays(messageOverdueDays);
        return task;
    }

    @Bean
    public XkwRocketmqMessageService xkwRocketmqMessageService() {
        return new XkwRocketmqMessageServiceImpl();
    }

    @Bean
    public XkwRocketmqConsumerService xkwRocketmqConsumerService(RocketMQProperties rocketMQProperties) {
        XkwRocketmqConsumerServiceImpl consumerService = new XkwRocketmqConsumerServiceImpl();
        consumerService.setNamespace(rocketMQProperties.getConsumer().getNamespace());
        return consumerService;
    }

    @Configuration
    @ConditionalOnProperty(
            name = "xkw-rocketmq.consumer.data-source-type",
            havingValue = "mysql_innodb",
            matchIfMissing = true
    )
    public static class MysqlInnoDBConfiguration {

        @Bean
        public XkwRocketmqConsumerRepository xkwRocketmqConsumerRepository() {
            return new XkwRocketmqConsumerRepositoryMysqlImpl();
        }

        @Bean
        public XkwRocketmqConsumerErrorLogRepository xkwRocketmqConsumerErrorLogRepository() {
            return new XkwRocketmqConsumerErrorLogRepositoryMysqlImpl();
        }

        @Bean
        @ConditionalOnMissingBean
        public XkwRocketmqLockRepository xkwRocketmqLockRepository() {
            return new XkwRocketmqLockRepositoryMysqlImpl();
        }
    }
}
