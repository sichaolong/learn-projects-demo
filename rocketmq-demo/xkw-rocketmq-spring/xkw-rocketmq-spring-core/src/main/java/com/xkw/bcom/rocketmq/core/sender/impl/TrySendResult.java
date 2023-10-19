/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.sender.impl;

/**
 * TrySendResult
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月17日
 */
enum TrySendResult {

    /**
     * 发送成功
     */
    SUCCESS,
    /**
     * 发送失败
     */
    FAILURE,
    /**
     * 未发送
     */
    UN_SEND
}
