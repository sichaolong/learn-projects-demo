/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.autoconfigure.config;

import com.xkw.bcom.rocketmq.core.constant.XkwRocketmqConstant;
import com.xkw.bcom.rocketmq.core.serializer.XkwRocketmqSerializer;
import com.xkw.bcom.rocketmq.core.serializer.impl.XkwRocketmqSerializerJacksonImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * XkwRocketmqCommonConfiguration
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月20日
 */
@Configuration
public class XkwRocketmqCommonConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(XkwRocketmqCommonConfiguration.class);

    @PostConstruct
    public void init() {
        LOGGER.info("xkw rocketmq spring instance id: {}", XkwRocketmqConstant.INSTANCE);
    }

    @Bean
    @ConditionalOnMissingBean
    public XkwRocketmqSerializer<String> xkwRocketmqSerializer() {
        return new XkwRocketmqSerializerJacksonImpl();
    }
}
