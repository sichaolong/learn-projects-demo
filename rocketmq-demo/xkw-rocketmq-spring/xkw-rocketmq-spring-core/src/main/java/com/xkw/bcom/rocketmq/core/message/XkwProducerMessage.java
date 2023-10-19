/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.message;

/**
 * XkwProducerMessage
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月14日
 */
public class XkwProducerMessage {

    /**
     * 自增id
     */
    private Long id;
    /**
     * key前缀
     */
    private String keyPrefix;
    /**
     * 名称空间，与配置文件一致，无法修改，用于隔离消息
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
     * 消息类型
     */
    private XkwMessageType type;
    /**
     * 顺序消息根据hashKey进行散列
     */
    private String hashKey;
    /**
     * 延迟消息的延迟等级
     */
    private Integer delayLevel;
    /**
     * 消息内容
     */
    private String payload;
    /**
     * 消息状态
     */
    private XkwProducerMessageStatus status;
    /**
     * 重发次数
     */
    private Integer retryTimes;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 成功发送时间
     */
    private Long commitTime;
    /**
     * 消息内容对象
     */
    private Object payloadObject;

    XkwProducerMessage() {
        this.status = XkwProducerMessageStatus.P0;
        this.retryTimes = 0;
        this.createTime = System.currentTimeMillis();
    }

    XkwProducerMessage(String topic, String tag, XkwMessageType type, String hashKey, Integer delayLevel, Object payloadObject) {
        this.topic = topic;
        this.tag = tag;
        this.type = type;
        this.hashKey = hashKey;
        this.delayLevel = delayLevel;
        this.payloadObject = payloadObject;

        this.status = XkwProducerMessageStatus.P0;
        this.retryTimes = 0;
        this.createTime = System.currentTimeMillis();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
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

    public XkwMessageType getType() {
        return type;
    }

    public void setType(XkwMessageType type) {
        this.type = type;
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    public Integer getDelayLevel() {
        return delayLevel;
    }

    public void setDelayLevel(Integer delayLevel) {
        this.delayLevel = delayLevel;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public XkwProducerMessageStatus getStatus() {
        return status;
    }

    public void setStatus(XkwProducerMessageStatus status) {
        this.status = status;
    }

    public Integer getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(Integer retryTimes) {
        this.retryTimes = retryTimes;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(Long commitTime) {
        this.commitTime = commitTime;
    }

    public Object getPayloadObject() {
        return payloadObject;
    }

    public void setPayloadObject(Object payloadObject) {
        this.payloadObject = payloadObject;
    }
}
