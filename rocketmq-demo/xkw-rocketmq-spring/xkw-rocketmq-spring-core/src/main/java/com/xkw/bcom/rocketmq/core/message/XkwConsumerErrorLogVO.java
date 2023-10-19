/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.message;

/**
 * XkwConsumerErrorLogVO
 * <p>因为要返回给前端，所以使用String类型替换Long类型，时间戳除外
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年07月01日
 */
public class XkwConsumerErrorLogVO {

    private String id;
    /**
     * 消费者消息id
     */
    private String messageId;
    /**
     * 异常时间
     */
    private Long createTime;
    /**
     * 异常日志
     */
    private String errorLog;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
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
