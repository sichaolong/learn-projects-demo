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
 * IntMessageHandler
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月28日
 */
@XkwMessageHandler(tags = "int")
@Slf4j
public class IntMessageHandler extends AbstractXkwIdCheckMessageHandler<Integer> {

    @Override
    public void handleMessage(Integer body, XkwConsumerMessage message, MessageExt messageExt) {
        log.info("int message body: {}", body);
    }
}
