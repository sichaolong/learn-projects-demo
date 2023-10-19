/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.consumer.demo.handler;

import com.xkw.bcom.rocketmq.consumer.demo.model.TestModel;
import com.xkw.bcom.rocketmq.core.listener.AbstractXkwIdCheckMessageHandler;
import com.xkw.bcom.rocketmq.core.listener.XkwMessageHandler;
import com.xkw.bcom.rocketmq.core.message.XkwConsumerMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * OrderMessageHandler
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年07月05日
 */
@XkwMessageHandler(tags = "order")
@Slf4j
public class OrderMessageHandler extends AbstractXkwIdCheckMessageHandler<TestModel> {

    @Override
    public void handleMessage(TestModel body, XkwConsumerMessage message, MessageExt messageExt) {
        log.info("start order message, body: {}", body);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignore) {
        }
        log.info("finish order message body: {}", body);
    }
}
