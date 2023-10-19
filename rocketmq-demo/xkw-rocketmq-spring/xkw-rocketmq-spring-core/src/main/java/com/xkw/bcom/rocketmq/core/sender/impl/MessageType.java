/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.sender.impl;

import com.xkw.bcom.rocketmq.core.message.XkwMessageType;

import java.lang.annotation.*;

/**
 * MessageType
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月23日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@interface MessageType {

    XkwMessageType value();
}
