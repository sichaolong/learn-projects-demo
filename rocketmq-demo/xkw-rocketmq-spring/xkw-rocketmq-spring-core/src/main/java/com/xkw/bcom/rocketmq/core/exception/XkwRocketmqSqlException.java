/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.exception;

/**
 * XkwRocketmqSqlException
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月16日
 */
public class XkwRocketmqSqlException extends RuntimeException {

    public XkwRocketmqSqlException() {
    }

    public XkwRocketmqSqlException(String message) {
        super(message);
    }

    public XkwRocketmqSqlException(String message, Throwable cause) {
        super(message, cause);
    }

    public XkwRocketmqSqlException(Throwable cause) {
        super(cause);
    }
}
