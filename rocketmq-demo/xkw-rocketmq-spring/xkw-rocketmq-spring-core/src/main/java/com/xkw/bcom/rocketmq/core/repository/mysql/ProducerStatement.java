/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.repository.mysql;

/**
 * ProducerStatement
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月16日
 */
interface ProducerStatement {

    String INSERT = "INSERT INTO xkw_producer_message (key_prefix, namespace, topic, tag, type, hash_key, delay_level, payload, status, retry_times, create_time, commit_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    String LIST_BY_IDS = "SELECT id, key_prefix, namespace, topic, tag, type, hash_key, delay_level, payload, status, retry_times, create_time, commit_time FROM xkw_producer_message WHERE id IN (%s)";

    String UPDATE_STATUS_BY_IDS = "UPDATE xkw_producer_message SET status = ? WHERE id IN (%s)";

    String UPDATE_STATUS_AND_COMMIT_TIME_BY_IDS = "UPDATE xkw_producer_message SET status = ?, commit_time = ? WHERE id IN (%s)";

    String UPDATE_STATUS_AND_INCREASE_RETRY_TIMES_BY_IDS = "UPDATE xkw_producer_message SET status = ?, retry_times = retry_times + 1 WHERE id IN (%s)";

    String LIST_IDS_BY_STATUS_AND_CREATE_TIME = "SELECT id FROM xkw_producer_message WHERE id > ? AND namespace = ? AND status = ? AND create_time < ? ORDER BY id LIMIT ?";

    String DELETE_BY_IDS = "DELETE FROM xkw_producer_message WHERE id IN (%s)";

    String LIST_UNSENT = "SELECT id, key_prefix, namespace, topic, tag, type, hash_key, delay_level, payload, status, retry_times, create_time, commit_time FROM xkw_producer_message WHERE id > ? AND namespace = ? AND retry_times <= ? AND (status = 1 OR (status = 0 AND create_time < ?)) ORDER BY id LIMIT ?";

    String FIND_FIRST_UNSENT_BY_HASH_KEY = "SELECT id, key_prefix, namespace, topic, tag, type, hash_key, delay_level, payload, status, retry_times, create_time, commit_time FROM xkw_producer_message WHERE namespace = ? AND type = ? AND hash_key = ? AND retry_times <= ? AND status IN (0, 1) ORDER BY id LIMIT 1";

    String LIST_BY_MIN_ID_AND_STATUS_AND_RETRY_TIMES = "SELECT id, key_prefix, namespace, topic, tag, type, hash_key, delay_level, payload, status, retry_times, create_time, commit_time FROM xkw_producer_message WHERE id > ? AND namespace = ? AND status = ? AND retry_times > ? ORDER BY id LIMIT ?";
}
