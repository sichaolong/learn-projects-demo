/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.repository.mysql;

/**
 * ConsumerErrorLogStatement
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月28日
 */
interface ConsumerErrorLogStatement {

    String INSERT = "INSERT INTO xkw_consumer_error_log (message_id, create_time, error_log) VALUES (?, ?, ?)";

    String DELETE_BY_IDS = "DELETE FROM xkw_consumer_error_log WHERE id IN (%s)";

    String LIST_IDS_BY_MESSAGE_ID = "SELECT id FROM xkw_consumer_error_log WHERE message_id = ?";

    String LIST_IDS_BY_MESSAGE_IDS = "SELECT id FROM xkw_consumer_error_log WHERE message_id IN (%s)";

    String LIST_BY_MESSAGE_IDS = "SELECT id, message_id, create_time, error_log FROM xkw_consumer_error_log WHERE message_id IN (%s) ORDER BY id DESC";
}
