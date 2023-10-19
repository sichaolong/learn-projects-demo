/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.message;

import java.util.HashMap;
import java.util.Map;

/**
 * XkwProducerMessageStatus
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月14日
 */
public enum XkwProducerMessageStatus {

    P0(0, "发送中"),
    P1(1, "发送失败"),
    P2(2, "发送成功");

    private final int code;
    private final String desc;

    static final Map<Integer, XkwProducerMessageStatus> CODE_MAP = new HashMap<>();

    static {
        for (XkwProducerMessageStatus value : XkwProducerMessageStatus.values()) {
            CODE_MAP.put(value.getCode(), value);
        }
    }

    public static XkwProducerMessageStatus fromCode(int code) {
        return CODE_MAP.get(code);
    }

    XkwProducerMessageStatus(int code, String desc) {
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
