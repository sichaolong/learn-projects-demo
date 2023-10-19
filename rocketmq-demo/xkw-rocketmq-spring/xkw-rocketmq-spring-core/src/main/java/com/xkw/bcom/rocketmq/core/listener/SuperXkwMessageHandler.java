/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.listener;

import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * SuperXkwMessageHandler
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年08月15日
 */
public abstract class SuperXkwMessageHandler<T> implements IMessageHandler<T> {

    private TypeReference<T> payloadType;

    {
        // 取第一个泛型参数作为payloadType，Handler实现类需要注意规范
        Class<?> clazz = this.getClass();
        while (clazz != Object.class) {
            try {
                ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                this.payloadType = new TypeReferenceImpl(actualTypeArguments[0]);
                break;
            } catch (Exception e) {
                clazz = clazz.getSuperclass();
            }
        }
    }

    @Override
    public TypeReference<T> payloadType() {
        return this.payloadType;
    }

    private class TypeReferenceImpl extends TypeReference<T> {

        public final Type type;

        public TypeReferenceImpl(Type type) {
            this.type = type;
        }

        @Override
        public Type getType() {
            return this.type;
        }
    }
}
