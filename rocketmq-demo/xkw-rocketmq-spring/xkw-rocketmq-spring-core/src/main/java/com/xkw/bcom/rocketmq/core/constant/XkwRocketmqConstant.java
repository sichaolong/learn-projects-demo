/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.constant;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * XkwRocketmqConstant
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月17日
 */
public interface XkwRocketmqConstant {

    String INSTANCE = UUID.randomUUID().toString();

    int BATCH_SIZE = 100;

    String PLACEHOLDER = "?";

    String DELIMITER = ",";

    String PLACEHOLDER_BATCH = StringUtils.join(new ArrayList<String>(BATCH_SIZE) {
        {
            for (int i = 0; i < BATCH_SIZE; i++) {
                this.add(PLACEHOLDER);
            }
        }
    }, DELIMITER);

    String DESTINATION = "%s:%s";

    String DATE_PATTERN = "yyyy-MM-dd";

    String TIME_PATTERN = "HH:mm:ss.SSS";

    String DATE_TIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

    String BLANK = "";

    String NAMESPACE_SUFFIX = "%";

    TransactionDefinition TRANSACTION_DEFINITION = new DefaultTransactionDefinition();

    /**
     * 在已知list长度时使用，避免频繁扩容
     */
    static <T> Collector<T, ?, List<T>> toList(int size) {
        return Collectors.toCollection(() -> new ArrayList<>(size));
    }
}
