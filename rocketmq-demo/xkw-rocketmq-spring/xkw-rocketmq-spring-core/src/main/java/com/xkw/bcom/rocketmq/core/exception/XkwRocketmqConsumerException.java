/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.exception;

/**
 * XkwConsumerException
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月16日
 */
public class XkwRocketmqConsumerException extends RuntimeException {

    public XkwRocketmqConsumerException() {
    }

    public XkwRocketmqConsumerException(String message) {
        super(message);
    }

    public XkwRocketmqConsumerException(String message, Throwable cause) {
        super(message, cause);
    }

    public XkwRocketmqConsumerException(Throwable cause) {
        super(cause);
    }
}
