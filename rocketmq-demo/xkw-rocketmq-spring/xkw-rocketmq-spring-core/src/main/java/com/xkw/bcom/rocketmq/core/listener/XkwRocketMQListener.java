/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.listener;

import com.xkw.bcom.rocketmq.core.constant.XkwRocketmqConstant;
import com.xkw.bcom.rocketmq.core.exception.XkwRocketmqConsumerException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.*;

/**
 * XkwRocketMQListener
 * 监听并分发消息
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月11日
 */
@SuppressWarnings("rawtypes")
public abstract class XkwRocketMQListener implements RocketMQListener<MessageExt>, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(XkwRocketMQListener.class);

    private final Map<String, IMessageHandler> handlerMap = new HashMap<>();

    /**
     * {@link RocketMQMessageListener#namespace()}
     */
    private String namespace;
    /**
     * {@link RocketMQMessageListener#consumeTimeout()} * 60 * 1000L
     */
    private long consumeTimeout;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RocketMQMessageListener listenerAnnotation = this.getClass().getDeclaredAnnotation(RocketMQMessageListener.class);
        this.consumeTimeout = listenerAnnotation.consumeTimeout()  * 60 * 1000L;
        this.namespace = applicationContext.getEnvironment().resolveRequiredPlaceholders(listenerAnnotation.namespace());

        this.initHandlers(applicationContext);
    }

    @Override
    public void onMessage(MessageExt messageExt) {
        String topic = messageExt.getTopic();
        String tag = messageExt.getTags();
        if (!filterTag(tag)) {
            if (LOGGER.isDebugEnabled()) {
                List<String> acceptTags = this.acceptTags();
                List<String> denyTags = this.denyTags();
                String acceptTagsJoin = acceptTags == null ? "null" : String.join(XkwRocketmqConstant.DELIMITER, acceptTags);
                String denyTagsJoin = denyTags == null ? "null" : String.join(XkwRocketmqConstant.DELIMITER, denyTags);
                LOGGER.debug("listener: {}, acceptTags: {}, denyTags: {}, skip message, uniqueKey: {}, topic: {}, tag: {}",
                        this.getClass().getName(), acceptTagsJoin, denyTagsJoin, messageExt.getKeys(), messageExt.getTopic(), messageExt.getTags());
            }
            return;
        }
        IMessageHandler handler = this.handlerMap.get(tag);
        if (handler == null) {
            throw new XkwRocketmqConsumerException(String.format("cannot find message handler, topic: [%s] tag: [%s]", topic, tag));
        }

        handler.handleMessage(messageExt, namespace, consumeTimeout);
    }

    /**
     * 返回当前listener接受的tag，默认返回null，不进行过滤
     * <p>当返回值不为null时，忽略{@link #denyTags()}
     * <p>不建议和{@link #denyTags()}同时使用
     *
     * @return tag集合
     */
    protected List<String> acceptTags() {
        return null;
    }

    /**
     * 返回当前listener拒绝的tag，默认返回null，不进行过滤
     * <p>只有当{@link #acceptTags()}返回null时，此方法生效
     * <p>不建议和{@link #acceptTags()}同时使用
     *
     * @return tag集合
     */
    protected List<String> denyTags() {
        return null;
    }

    private boolean filterTag(String tag) {
        List<String> acceptTags = this.acceptTags();
        if (acceptTags != null) {
            return acceptTags.contains(tag);
        }
        List<String> denyTags = this.denyTags();
        if (denyTags != null) {
            return !denyTags.contains(tag);
        }
        return true;
    }

    private void initHandlers(ApplicationContext applicationContext) {
        RocketMQMessageListener listenerAnnotation = this.getClass().getDeclaredAnnotation(RocketMQMessageListener.class);
        String topic = listenerAnnotation.topic();
        Map<String, IMessageHandler> handlers = applicationContext.getBeansOfType(IMessageHandler.class);
        for (IMessageHandler handler : handlers.values()) {
            XkwMessageHandler handlerAnnotation = handler.getClass().getDeclaredAnnotation(XkwMessageHandler.class);
            if (handlerAnnotation == null) {
                continue;
            }
            String[] topics = handlerAnnotation.topics();
            if (topics.length > 0 && !Arrays.asList(topics).contains(topic)) {
                continue;
            }
            String[] tags = handlerAnnotation.tags();
            for (String tag : tags) {
                this.handlerMap.put(tag, handler);
            }
        }
    }
}
