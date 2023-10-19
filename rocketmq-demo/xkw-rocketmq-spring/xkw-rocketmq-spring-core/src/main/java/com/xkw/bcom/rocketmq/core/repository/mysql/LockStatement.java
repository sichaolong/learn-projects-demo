/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.repository.mysql;

/**
 * LockStatement
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月21日
 */
interface LockStatement {

    String GET_BY_ID = "SELECT id, instance, create_time FROM xkw_rocketmq_lock WHERE id = ?";

    String ACQUIRE_INSERT = "INSERT INTO xkw_rocketmq_lock (id, instance, create_time) VALUES (?, ?, ?)";

    String ACQUIRE_UPDATE = "UPDATE xkw_rocketmq_lock SET instance = ?, create_time = ? WHERE id = ? AND create_time < ?";

    String RELEASE = "DELETE FROM xkw_rocketmq_lock WHERE id = ? AND instance = ? AND create_time >= ?";

    String KEEPALIVE = "UPDATE xkw_rocketmq_lock SET create_time = ? WHERE id = ? AND instance = ? AND create_time >= ?";
}
