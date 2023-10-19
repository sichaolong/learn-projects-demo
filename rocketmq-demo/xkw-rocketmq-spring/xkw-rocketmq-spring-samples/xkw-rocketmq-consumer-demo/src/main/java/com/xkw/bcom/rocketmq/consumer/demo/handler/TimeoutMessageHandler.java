/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.consumer.demo.handler;

import com.xkw.bcom.rocketmq.core.listener.AbstractXkwIdCheckMessageHandler;
import com.xkw.bcom.rocketmq.core.listener.XkwMessageHandler;
import com.xkw.bcom.rocketmq.core.message.XkwConsumerMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * TimeoutMessageHandler
 * 测试RocketMQ对消费超时的处理
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年11月14日
 */
@Slf4j
@XkwMessageHandler(tags = "timeout")
public class TimeoutMessageHandler extends AbstractXkwIdCheckMessageHandler<String> {

    @Override
    public void handleMessage(String payload, XkwConsumerMessage consumerMessage, MessageExt messageExt) {
        for (int i = 0; i < 15; i++) {
            log.info("consume timeout message: {}, times: {}", payload, i);
            try {
                // 等10秒
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException("interrupted", e);
            }
        }
    }
}
