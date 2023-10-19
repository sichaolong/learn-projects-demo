/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.service.impl;

import com.xkw.bcom.rocketmq.core.message.XkwProducerMessage;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessageStatus;
import com.xkw.bcom.rocketmq.core.repository.XkwRocketmqProducerRepository;
import com.xkw.bcom.rocketmq.core.sender.impl.XkwDelegatorMessageSender;
import com.xkw.bcom.rocketmq.core.service.XkwRocketmqProducerService;

import javax.annotation.Resource;
import java.util.List;

/**
 * XkwRocketmqProducerServiceImpl
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月25日
 */
public class XkwRocketmqProducerServiceImpl implements XkwRocketmqProducerService {

    @Resource
    private XkwRocketmqProducerRepository producerRepository;
    @Resource
    private XkwDelegatorMessageSender delegatorMessageSender;

    private String namespace;
    private int maxRetryTimes;

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    @Override
    public List<XkwProducerMessage> listFailedMessages(long minId, int limit) {
        return producerRepository.listByMinIdAndStatusAndRetryTimes(namespace, minId, XkwProducerMessageStatus.P1, maxRetryTimes, limit);
    }

    @Override
    public void resendMessages(List<Long> ids) {
        List<XkwProducerMessage> messages = producerRepository.listByIds(ids);
        delegatorMessageSender.send(messages);
    }
}
