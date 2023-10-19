/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.serializer;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * XkwRocketmqSerializer
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月20日
 */
public interface XkwRocketmqSerializer<R> {

    R serialize(Object object);

    <T> T deserialize(R value, TypeReference<T> typeReference);
}
