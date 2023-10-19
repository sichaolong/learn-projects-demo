/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.message;

/**
 * XkwRocketmqLock
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月22日
 */
public class XkwRocketmqLock {

    private String id;
    private String instance;
    private Long createTime;

    public XkwRocketmqLock() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
