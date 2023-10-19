/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.autoconfigure.condition;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

/**
 * OnPropertyGreaterThanCondition
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月21日
 */
public class OnPropertyGreaterThanCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> map = metadata.getAnnotationAttributes(ConditionalOnPropertyGreaterThan.class.getName());
        if (map == null) {
            return false;
        }
        String propertyName = (String) map.get("value");
        long target = (Long) map.get("target");
        boolean defaultValue = (Boolean) map.get("defaultValue");
        String property = context.getEnvironment().getProperty(propertyName);
        if (StringUtils.isBlank(property)) {
            return defaultValue;
        }
        long intVal = Long.parseLong(property);
        return intVal > target;
    }
}
