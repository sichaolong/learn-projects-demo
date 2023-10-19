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
 * StringMessageListener
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月23日
 */
@XkwMessageHandler(tags = "string")
@Slf4j
public class StringMessageHandler extends AbstractXkwIdCheckMessageHandler<String> {

    @Override
    public void handleMessage(String body, XkwConsumerMessage message, MessageExt messageExt) {
        log.info("string message body: {}", body);
    }
}
