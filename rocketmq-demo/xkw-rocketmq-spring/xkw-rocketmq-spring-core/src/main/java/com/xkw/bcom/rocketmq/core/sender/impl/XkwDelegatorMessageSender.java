/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.sender.impl;

import com.xkw.bcom.rocketmq.core.message.XkwMessageType;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessage;
import com.xkw.bcom.rocketmq.core.sender.XkwMessageSenderStrategy;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.*;
import java.util.stream.Collectors;

/**
 * XkwDelegatorMessageSender
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月22日
 */
public class XkwDelegatorMessageSender implements XkwMessageSenderStrategy, ApplicationContextAware {

    private static final Map<XkwMessageType, XkwMessageSenderStrategy> DELEGATES = new HashMap<>(8);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, XkwMessageSenderStrategy> map = applicationContext.getBeansOfType(XkwMessageSenderStrategy.class);
        for (XkwMessageSenderStrategy delegate : map.values()) {
            MessageType typeAnno = delegate.getClass().getAnnotation(MessageType.class);
            if (typeAnno == null) {
                continue;
            }
            XkwMessageType type = typeAnno.value();
            DELEGATES.put(type, delegate);
        }
    }

    @Override
    public boolean send(List<XkwProducerMessage> messages) {
        // 不同消息类型的发送逻辑不同，按照消息类型分组
        Map<XkwMessageType, List<XkwProducerMessage>> messageMap = messages.stream()
                .collect(Collectors.groupingBy(XkwProducerMessage::getType));
        boolean result = true;
        for (Map.Entry<XkwMessageType, List<XkwProducerMessage>> entry : messageMap.entrySet()) {
            XkwMessageSenderStrategy delegate = DELEGATES.get(entry.getKey());
            if (delegate.send(entry.getValue())) {
                continue;
            }
            result = false;
        }
        return result;
    }
}
