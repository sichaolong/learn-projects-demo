/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.message;

/**
 * XkwConsumerMessage
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月14日
 */
public class XkwConsumerMessage {

    /**
     * 消息id，由生产者设置
     */
    private Long id;
    /**
     * 生产者为消息设置的唯一标识
     */
    private String uniqueKey;
    /**
     * 名称空间，用于隔离消息
     */
    private String namespace;
    /**
     * 主题
     */
    private String topic;
    /**
     * 标签，用于过滤消息
     */
    private String tag;
    /**
     * 消息内容
     */
    private String payload;
    /**
     * 消息状态
     */
    private XkwConsumerMessageStatus status;
    /**
     * 重新消费次数
     */
    private Integer retryTimes;
    /**
     * 开始时间
     */
    private Long startTime;
    /**
     * 结束时间
     */
    private Long finishTime;

    public XkwConsumerMessage() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public XkwConsumerMessageStatus getStatus() {
        return status;
    }

    public void setStatus(XkwConsumerMessageStatus status) {
        this.status = status;
    }

    public Integer getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(Integer retryTimes) {
        this.retryTimes = retryTimes;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Long finishTime) {
        this.finishTime = finishTime;
    }
}
