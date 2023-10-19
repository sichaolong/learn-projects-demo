/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.service.impl;

import com.xkw.bcom.rocketmq.core.exception.XkwRocketmqSqlException;
import com.xkw.bcom.rocketmq.core.message.XkwConsumerErrorLog;
import com.xkw.bcom.rocketmq.core.message.XkwConsumerMessage;
import com.xkw.bcom.rocketmq.core.message.XkwConsumerMessageStatus;
import com.xkw.bcom.rocketmq.core.repository.XkwRocketmqConsumerErrorLogRepository;
import com.xkw.bcom.rocketmq.core.repository.XkwRocketmqConsumerRepository;
import com.xkw.bcom.rocketmq.core.service.XkwRocketmqMessageService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.BeanUtils;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.ContentTypeResolver;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MimeType;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * XkwRocketmqMessageServiceImpl
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月24日
 */
public class XkwRocketmqMessageServiceImpl implements XkwRocketmqMessageService {

    private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();
    private static final ContentTypeResolver CONTENT_TYPE_RESOLVER = new DefaultContentTypeResolver();
    private static final String CONTENT_TYPE = "contentType";

    @Resource
    private XkwRocketmqConsumerRepository consumerRepository;
    @Resource
    private XkwRocketmqConsumerErrorLogRepository consumerErrorLogRepository;

    @Override
    public XkwConsumerMessage parseMessage(MessageExt messageExt, String namespace) {
        XkwConsumerMessage message = new XkwConsumerMessage();
        String key = messageExt.getKeys();
        message.setUniqueKey(key);
        message.setNamespace(namespace);
        message.setTopic(messageExt.getTopic());
        message.setTag(messageExt.getTags());
        String payload = new String(messageExt.getBody(), getCharset(messageExt));
        message.setPayload(payload);
        message.setStatus(XkwConsumerMessageStatus.P0);
        message.setRetryTimes(0);
        message.setStartTime(System.currentTimeMillis());
        message.setFinishTime(0L);
        return message;
    }

    @Override
    public void addErrorLog(XkwConsumerMessage message, Throwable throwable) {
        String log = ExceptionUtils.getStackTrace(throwable);
        XkwConsumerErrorLog errorLog = new XkwConsumerErrorLog(message.getId(), log);
        consumerErrorLogRepository.save(errorLog);
    }

    @Override
    public void cleanErrorLog(XkwConsumerMessage message) {
        List<Long> ids = consumerErrorLogRepository.listIdsByMessageId(message.getId());
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        consumerErrorLogRepository.deleteByIds(ids);
    }

    @Override
    public boolean shouldConsume(XkwConsumerMessage message, long consumeTimeout) {
        // 1. 从数据查询消息
        XkwConsumerMessage messageInDB = consumerRepository.findByKey(message.getUniqueKey());
        long now = System.currentTimeMillis();
        long minStartTime = now - consumeTimeout;
        if (messageInDB == null) {
            // 1.1 消息在表中不存在，插入消息，状态为消费中，开始时间为当前时间
            boolean canConsume = insertPending(message);
            return checkCanConsume(message, canConsume);
        }
        message.setId(messageInDB.getId());
        if (messageInDB.getStatus() == XkwConsumerMessageStatus.P1) {
            // 1.2 消息在表中存在，状态为失败，更新消息状态为消费中，开始时间为当前时间，重试次数+1
            boolean canConsume = markPending(message, messageInDB);
            return checkCanConsume(message, canConsume);
        }
        if (messageInDB.getStatus() == XkwConsumerMessageStatus.P0) {
            if (messageInDB.getStartTime() <= minStartTime) {
                // 1.3 消息在表中存在，状态为消费中，开始时间 <= (当前时间 - expireMillis)，更新消息状态为消费中，开始时间为当前时间，重试次数+1
                boolean canConsume = markPending(message, messageInDB);
                return checkCanConsume(message, canConsume);
            } else {
                // 1.4 消息在表中存在，状态为消费中，开始时间 > (当前时间 - expireMillis)，[不可消费]
                return checkCanConsume(message, false);
            }
        }
        // 1.5 消息在表中存在，状态为成功，[不可消费]
        return checkCanConsume(message, false);
    }

    @Override
    public boolean markFailure(XkwConsumerMessage message) {
        XkwConsumerMessage condition = new XkwConsumerMessage();
        BeanUtils.copyProperties(message, condition);
        message.setStatus(XkwConsumerMessageStatus.P1);
        return consumerRepository.markFailure(message, condition);
    }

    @Override
    public boolean markSuccess(XkwConsumerMessage message) {
        XkwConsumerMessage condition = new XkwConsumerMessage();
        BeanUtils.copyProperties(message, condition);
        message.setStatus(XkwConsumerMessageStatus.P2);
        message.setFinishTime(System.currentTimeMillis());
        /*
         * 考虑消费超时的情况，当前消费线程可能已经超时了
         * 那么就会有其他消费线程重新消费这条消息，所以开始时间也会被修改
         * 所以在这里修正开始时间
         */
        return consumerRepository.markSuccess(message, condition);
    }

    private boolean insertPending(XkwConsumerMessage message) {
        message.setStatus(XkwConsumerMessageStatus.P0);
        message.setStartTime(System.currentTimeMillis());
        try {
            Long id = consumerRepository.save(message);
            message.setId(id);
            return id != null;
        } catch (XkwRocketmqSqlException e) {
            return false;
        }
    }

    private boolean markPending(XkwConsumerMessage message, XkwConsumerMessage messageInDB) {
        XkwConsumerMessage condition = new XkwConsumerMessage();
        BeanUtils.copyProperties(messageInDB, condition);
        message.setStatus(XkwConsumerMessageStatus.P0);
        message.setStartTime(System.currentTimeMillis());
        message.setRetryTimes(messageInDB.getRetryTimes() + 1);
        return consumerRepository.markPendingIfNotSuccess(message, condition);
    }

    /**
     * 判断是否可消费
     */
    private boolean checkCanConsume(XkwConsumerMessage message, boolean canConsume) {
        if (canConsume) {
            return true;
        }
        // 重新查询消息
        XkwConsumerMessage messageInDB = consumerRepository.findByKey(message.getUniqueKey());
        // 刷新message属性
        BeanUtils.copyProperties(messageInDB, message);
        return false;
    }

    private Charset getCharset(MessageExt messageExt) {
        Map<String, Object> map = new HashMap<>();
        map.put(CONTENT_TYPE, messageExt.getProperty(CONTENT_TYPE));
        MessageHeaders headers = new MessageHeaders(map);
        MimeType mimeType = CONTENT_TYPE_RESOLVER.resolve(headers);
        if (mimeType == null) {
            return DEFAULT_CHARSET;
        }
        Charset charset = mimeType.getCharset();
        if (charset == null) {
            return DEFAULT_CHARSET;
        }
        return charset;
    }
}
