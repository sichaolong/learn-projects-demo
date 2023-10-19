/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.serializer.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.xkw.bcom.rocketmq.core.constant.XkwRocketmqConstant;
import com.xkw.bcom.rocketmq.core.exception.XkwRocketmqSerializerException;
import com.xkw.bcom.rocketmq.core.serializer.XkwRocketmqSerializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * XkwRocketmqSerializerJacksonImpl
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月20日
 */
public class XkwRocketmqSerializerJacksonImpl implements XkwRocketmqSerializer<String> {

    private final ObjectMapper objectMapper;

    public XkwRocketmqSerializerJacksonImpl() {
        this.objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(XkwRocketmqConstant.DATE_TIME_PATTERN)));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(XkwRocketmqConstant.DATE_PATTERN)));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(XkwRocketmqConstant.TIME_PATTERN)));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(XkwRocketmqConstant.DATE_TIME_PATTERN)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(XkwRocketmqConstant.DATE_PATTERN)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(XkwRocketmqConstant.TIME_PATTERN)));

        this.objectMapper.registerModule(javaTimeModule);
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    @Override
    public String serialize(Object object) {
        if (object == null) {
            return XkwRocketmqConstant.BLANK;
        }
        if (object instanceof String) {
            return object.toString();
        }
        try {
            return this.objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new XkwRocketmqSerializerException("serialize error", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(String value, TypeReference<T> typeReference) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        if (typeReference.getType() == String.class) {
            return (T) value;
        }
        try {
            return this.objectMapper.readValue(value, typeReference);
        } catch (IOException e) {
            throw new XkwRocketmqSerializerException("deserialize error", e);
        }
    }
}
