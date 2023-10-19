/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.autoconfigure.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * ConditionalOnPropertyGreaterThan
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月21日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
@Conditional(OnPropertyGreaterThanCondition.class)
public @interface ConditionalOnPropertyGreaterThan {

    String value();

    long target() default 0;

    boolean defaultValue() default true;
}
