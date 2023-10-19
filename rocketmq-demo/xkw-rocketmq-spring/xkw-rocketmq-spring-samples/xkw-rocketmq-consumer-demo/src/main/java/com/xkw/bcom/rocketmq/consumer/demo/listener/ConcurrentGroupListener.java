/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.consumer.demo.listener;

import com.xkw.bcom.rocketmq.consumer.demo.constant.ConsumerConstant;
import com.xkw.bcom.rocketmq.core.listener.XkwRocketMQListener;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ConcurrentGroupListener
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年07月05日
 */
@Component
@RocketMQMessageListener(
        topic = "BASEAPP-SCL-DEMO",
        consumerGroup = "concurrent",
        consumeTimeout = 1
)
public class ConcurrentGroupListener extends XkwRocketMQListener {

    @Override
    protected List<String> denyTags() {
        return ConsumerConstant.ORDER_TAGS;
    }
}
