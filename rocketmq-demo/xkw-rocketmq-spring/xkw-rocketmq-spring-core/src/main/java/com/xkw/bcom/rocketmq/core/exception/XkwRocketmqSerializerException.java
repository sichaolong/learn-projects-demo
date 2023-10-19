/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.exception;

/**
 * XkwRocketmqSerializerException
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月20日
 */
public class XkwRocketmqSerializerException extends RuntimeException {

    public XkwRocketmqSerializerException() {
    }

    public XkwRocketmqSerializerException(String message) {
        super(message);
    }

    public XkwRocketmqSerializerException(String message, Throwable cause) {
        super(message, cause);
    }

    public XkwRocketmqSerializerException(Throwable cause) {
        super(cause);
    }
}
