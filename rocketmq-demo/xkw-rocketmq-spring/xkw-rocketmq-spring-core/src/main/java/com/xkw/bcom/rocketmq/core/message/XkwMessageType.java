/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.message;

import java.util.HashMap;
import java.util.Map;

/**
 * XkwMessageType
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月14日
 */
public enum XkwMessageType {

    CONCURRENT(0, "并发消息"),
    DELAY(1, "延迟消息"),
    ORDER(2, "顺序消息");

    private final int code;
    private final String desc;

    static final Map<Integer, XkwMessageType> CODE_MAP = new HashMap<>();

    static {
        for (XkwMessageType value : XkwMessageType.values()) {
            CODE_MAP.put(value.getCode(), value);
        }
    }

    public static XkwMessageType fromCode(int code) {
        return CODE_MAP.get(code);
    }

    XkwMessageType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
