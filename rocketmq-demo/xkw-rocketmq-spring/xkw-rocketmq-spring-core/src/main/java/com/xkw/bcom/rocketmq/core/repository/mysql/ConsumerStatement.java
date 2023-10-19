/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.repository.mysql;

/**
 * ConsumerStatement
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月16日
 */
interface ConsumerStatement {

    String INSERT = "INSERT INTO xkw_consumer_message (unique_key, namespace, topic, tag, payload, status, retry_times, start_time, finish_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

    String MARK_PENDING_IF_NOT_SUCCESS = "UPDATE xkw_consumer_message SET status = 0, start_time = ?, retry_times = ? WHERE id = ? AND status <> 2 AND retry_times = ?";

    String MARK_FAILURE = "UPDATE xkw_consumer_message SET status = ? WHERE id = ? AND status = ? AND retry_times = ?";

    String MARK_SUCCESS = "UPDATE xkw_consumer_message SET status = ?, start_time = ?, finish_time = ? WHERE id = ?";

    String FIND_BY_KEY = "SELECT id, unique_key, namespace, topic, tag, payload, status, retry_times, start_time, finish_time FROM xkw_consumer_message WHERE unique_key = ?";

    String LIST_IDS_BY_STATUS_AND_START_TIME = "SELECT id FROM xkw_consumer_message WHERE id > ? AND namespace = ? AND status = ? AND start_time < ? ORDER BY id LIMIT ?";

    String DELETE_BY_IDS = "DELETE FROM xkw_consumer_message WHERE id IN (%s)";

    String LIST_BY_CONDITIONS = "SELECT id, unique_key, namespace, topic, tag, payload, status, retry_times, start_time, finish_time FROM xkw_consumer_message WHERE id > ? AND namespace = ? %s AND status = ? ORDER BY id LIMIT ?";

    String COUNT_BY_CONDITIONS = "SELECT count(*) FROM xkw_consumer_message WHERE namespace = ? %s AND status = ?";
}
