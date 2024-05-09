package com.tal.ailab.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

public class DateUtil {

    /**
     * 获取当前时间（东8区）
     * @return
     */
    public static Date getCurrentDate(){
        LocalDateTime localDateTime = LocalDateTime.now(ZoneOffset.of("+8"));
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        Date date = Date.from(instant);
        return date;
    }


}
