/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.message;

import java.util.HashMap;
import java.util.Map;

/**
 * XkwConsumerMessageStatus
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月14日
 */
public enum XkwConsumerMessageStatus {

    P0(0, "消费中"),
    P1(1, "消费失败"),
    P2(2, "消费成功");

    private final int code;
    private final String desc;

    static final Map<Integer, XkwConsumerMessageStatus> CODE_MAP = new HashMap<>();

    static {
        for (XkwConsumerMessageStatus value : XkwConsumerMessageStatus.values()) {
            CODE_MAP.put(value.getCode(), value);
        }
    }

    public static XkwConsumerMessageStatus fromCode(int code) {
        return CODE_MAP.get(code);
    }

    XkwConsumerMessageStatus(int code, String desc) {
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
