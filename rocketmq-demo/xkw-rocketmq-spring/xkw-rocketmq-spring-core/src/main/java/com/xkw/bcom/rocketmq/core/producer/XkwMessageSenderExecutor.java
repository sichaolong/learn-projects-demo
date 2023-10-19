/*
 * xkw.com Inc. Copyright (c) 2023 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.producer;

import com.xkw.bcom.rocketmq.core.message.XkwProducerMessage;

import java.util.List;

/**
 * XkwMessageSenderExecutor
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2023年05月06日
 */
public interface XkwMessageSenderExecutor {

    void execute(List<XkwProducerMessage> messages);
}
