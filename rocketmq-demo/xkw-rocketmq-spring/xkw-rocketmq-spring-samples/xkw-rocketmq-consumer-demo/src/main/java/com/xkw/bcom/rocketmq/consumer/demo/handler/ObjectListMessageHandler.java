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

import java.util.List;

/**
 * ObjectMessageListener
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2023年05月18日
 */
@XkwMessageHandler(tags = "objectList")
@Slf4j
public class ObjectListMessageHandler extends AbstractXkwIdCheckMessageHandler<List<TestModel>> {

    @Override
    public void handleMessage(List<TestModel> body, XkwConsumerMessage message, MessageExt messageExt) {
        log.info("object list message body: {}", body);
    }
}
