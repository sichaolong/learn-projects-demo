/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.listener;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * XkwMessageHandler
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年08月15日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface XkwMessageHandler {

    /**
     * 匹配的topic，默认空，表示所有topic
     */
    String[] topics() default {};

    /**
     * 匹配的tag
     */
    String[] tags();
}
