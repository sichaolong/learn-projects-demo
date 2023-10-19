/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.consumer.demo.listener;

import com.xkw.bcom.rocketmq.consumer.demo.constant.ConsumerConstant;
import com.xkw.bcom.rocketmq.core.listener.XkwRocketMQListener;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * OrderlyGroupListener
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年07月05日
 */
@Component
@RocketMQMessageListener(
        topic = "BASEAPP-SCL-DEMO",
        consumerGroup = "orderly",
        consumeMode = ConsumeMode.ORDERLY
)
public class OrderlyGroupListener extends XkwRocketMQListener {

    @Override
    protected List<String> acceptTags() {
        return ConsumerConstant.ORDER_TAGS;
    }
}
