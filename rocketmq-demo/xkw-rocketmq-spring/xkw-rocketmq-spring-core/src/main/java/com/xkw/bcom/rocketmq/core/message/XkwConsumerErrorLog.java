/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.message;

/**
 * XkwConsumerErrorLog
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月28日
 */
public class XkwConsumerErrorLog {

    private Long id;
    /**
     * 消费者消息id
     */
    private Long messageId;
    /**
     * 异常时间
     */
    private Long createTime;
    /**
     * 异常日志
     */
    private String errorLog;

    public XkwConsumerErrorLog() {
    }

    public XkwConsumerErrorLog(Long messageId, String errorLog) {
        this.messageId = messageId;
        this.errorLog = errorLog;
        this.createTime = System.currentTimeMillis();
    }

    public XkwConsumerErrorLog(Long id, Long messageId, Long createTime, String errorLog) {
        this.id = id;
        this.messageId = messageId;
        this.createTime = createTime;
        this.errorLog = errorLog;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getErrorLog() {
        return errorLog;
    }

    public void setErrorLog(String errorLog) {
        this.errorLog = errorLog;
    }
}
