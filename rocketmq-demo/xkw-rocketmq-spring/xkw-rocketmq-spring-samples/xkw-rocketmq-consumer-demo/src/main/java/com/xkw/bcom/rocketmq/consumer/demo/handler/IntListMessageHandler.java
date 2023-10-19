/*
 * xkw.com Inc. Copyright (c) 2023 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.consumer.demo.handler;

import com.xkw.bcom.rocketmq.core.listener.AbstractXkwIdCheckMessageHandler;
import com.xkw.bcom.rocketmq.core.listener.XkwMessageHandler;
import com.xkw.bcom.rocketmq.core.message.XkwConsumerMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * IntListMessageHandler
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2023年05月18日
 */
@XkwMessageHandler(tags = "intList")
@Slf4j
public class IntListMessageHandler extends AbstractXkwIdCheckMessageHandler<List<Integer>> {

    @Override
    public void handleMessage(List<Integer> payload, XkwConsumerMessage consumerMessage, MessageExt messageExt) {
        log.info("int list message body: {}", StringUtils.join(payload, ","));
    }
}
