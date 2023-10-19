/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.message;

/**
 * XkwConsumerErrorMessageVO
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月28日
 */
public class XkwConsumerErrorMessageVO {

    /**
     * 消息id，由生产者设置
     * <p>因为需要返回给前端，所以使用字符串类型
     */
    private String id;
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
     * <p>时间戳可以用很久，不转String
     */
    private Long startTime;
    /**
     * 最后一条异常日志
     */
    private XkwConsumerErrorLogVO errorLog;

    public XkwConsumerErrorMessageVO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public XkwConsumerErrorLogVO getErrorLog() {
        return errorLog;
    }

    public void setErrorLog(XkwConsumerErrorLogVO errorLog) {
        this.errorLog = errorLog;
    }
}
