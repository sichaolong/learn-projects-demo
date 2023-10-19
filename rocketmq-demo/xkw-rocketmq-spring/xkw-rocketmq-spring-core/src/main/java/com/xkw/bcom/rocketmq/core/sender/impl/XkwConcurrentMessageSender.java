/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.sender.impl;

import com.xkw.bcom.rocketmq.core.constant.XkwRocketmqConstant;
import com.xkw.bcom.rocketmq.core.message.XkwMessageType;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessage;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * XkwConcurrentMessageSender
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月17日
 */
@MessageType(XkwMessageType.CONCURRENT)
public class XkwConcurrentMessageSender extends AbstractXkwMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(XkwConcurrentMessageSender.class);

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public Map<TrySendResult, List<XkwProducerMessage>> trySend(List<MessageWrapper> wrappers) {
        // 批量消息要求destination一致
        Map<String, List<MessageWrapper>> destinationMap = wrappers.stream()
                .collect(Collectors.groupingBy(wrapper -> {
                    String topic = wrapper.getProducerMessage().getTopic();
                    String tag = wrapper.getProducerMessage().getTag();
                    return String.format(XkwRocketmqConstant.DESTINATION, topic, tag);
                }));

        Map<TrySendResult, List<XkwProducerMessage>> resultMap = new HashMap<>(8);

        for (Map.Entry<String, List<MessageWrapper>> entry : destinationMap.entrySet()) {
            String destination = entry.getKey();
            List<MessageWrapper> messageWrappers = entry.getValue();
            ListSplitter splitter = new ListSplitter(messageWrappers);
            while (splitter.hasNext()) {
                // 批量发送一个split
                List<MessageWrapper> split = splitter.next();
                List<XkwProducerMessage> splitMessages = split.stream()
                        .map(MessageWrapper::getProducerMessage)
                        .collect(XkwRocketmqConstant.toList(split.size()));
                TrySendResult result = trySendSplit(destination, split);
                // 合并发送结果
                List<XkwProducerMessage> messages = resultMap.computeIfAbsent(result, k -> new ArrayList<>());
                messages.addAll(splitMessages);
            }
        }

        return resultMap;
    }

    private TrySendResult trySendSplit(String destination, List<MessageWrapper> wrappers) {
        List<Message<String>> messages = wrappers.stream()
                .map(MessageWrapper::getMessage)
                .collect(XkwRocketmqConstant.toList(wrappers.size()));
        try {
            SendResult result = rocketMQTemplate.syncSend(destination, messages);
            SendStatus status = result.getSendStatus();
            if (LOGGER.isDebugEnabled()) {
                String ids = wrappers.stream()
                        .map(MessageWrapper::getProducerMessage)
                        .map(XkwProducerMessage::getId)
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));
                LOGGER.debug("destination: {}, status: {}, ids: [{}]", destination, status, ids);
            }
            if (status == SendStatus.SEND_OK) {
                return TrySendResult.SUCCESS;
            }
            return TrySendResult.FAILURE;
        } catch (Throwable e) {
            LOGGER.error("send concurrent message failure", e);
            return TrySendResult.FAILURE;
        }
    }

    /**
     * 参考：<a href="https://github.com/apache/rocketmq/blob/release-4.7.1/docs/cn/RocketMQ_Example.md#42-%E6%B6%88%E6%81%AF%E5%88%97%E8%A1%A8%E5%88%86%E5%89%B2">4.2 消息列表分割</a>
     */
    static class ListSplitter implements Iterator<List<MessageWrapper>> {
        private final int SIZE_LIMIT = 1024 * 1024 * 4;
        private final List<MessageWrapper> messages;
        private int currIndex;
        public ListSplitter(List<MessageWrapper> messages) {
            this.messages = messages;
        }
        @Override
        public boolean hasNext() {
            return currIndex < messages.size();
        }
        @Override
        public List<MessageWrapper> next() {
            int startIndex = getStartIndex();
            int nextIndex = startIndex;
            int totalSize = 0;
            for (; nextIndex < messages.size(); nextIndex++) {
                MessageWrapper message = messages.get(nextIndex);
                int tmpSize = calcMessageSize(message);
                if (tmpSize + totalSize > SIZE_LIMIT) {
                    break;
                } else {
                    totalSize += tmpSize;
                }
            }
            List<MessageWrapper> subList = messages.subList(startIndex, nextIndex);
            currIndex = nextIndex;
            return subList;
        }
        private int getStartIndex() {
            MessageWrapper currMessage = messages.get(currIndex);
            int tmpSize = calcMessageSize(currMessage);
            while(tmpSize > SIZE_LIMIT) {
                currIndex += 1;
                MessageWrapper message = messages.get(currIndex);
                tmpSize = calcMessageSize(message);
            }
            return currIndex;
        }

        /**
         * 文档中的案例是{@link org.apache.rocketmq.common.message.Message}对象
         * 现在还没有到那一步，获取不到该对象，提前转换有点浪费，所以估算一下
         */
        private int calcMessageSize(MessageWrapper message) {
            int tmpSize = message.getProducerMessage().getTopic().length()
                    + message.getProducerMessage().getPayload().getBytes(Charset.defaultCharset()).length;
            /*
             * 目前已知的properties
             * 1. KEYS：namespace + % + keyPrefix + 生产者消息id，最大长度30+1+20+23=74
             * 2. id：uuid，长度38
             * 3. WAIT：boolean，最大长度9
             * 4. contentType：目前都是text/plain;charset=UTF-8，长度35
             * 5. TAGS：自定义tag，按照数据库字段的限制50，最大长度54
             * 6. timestamp：long时间戳，最大长度28
             * 以上总和238
             * 文档上另外加了20字节的日志开销
             * 总共258
             * 保险起见，按350计算，防止有未知的property导致批量消息长度超出限制
             */
            tmpSize = tmpSize + 350;
            return tmpSize;
        }
    }
}
