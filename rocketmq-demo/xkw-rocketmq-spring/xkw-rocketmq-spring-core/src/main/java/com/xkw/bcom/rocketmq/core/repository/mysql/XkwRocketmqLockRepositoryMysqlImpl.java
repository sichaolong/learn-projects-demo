/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.repository.mysql;

import com.xkw.bcom.rocketmq.core.constant.XkwRocketmqConstant;
import com.xkw.bcom.rocketmq.core.message.XkwRocketmqLock;
import com.xkw.bcom.rocketmq.core.repository.XkwRocketmqLockRepository;
import com.xkw.bcom.rocketmq.core.util.SqlUtil;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

/**
 * XkwRocketmqLockRepositoryMysqlImpl
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月21日
 */
public class XkwRocketmqLockRepositoryMysqlImpl implements XkwRocketmqLockRepository {

    // @Resource
    private DataSource dataSource;

    @Override
    public boolean acquire(String id, long expireMillis) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            XkwRocketmqLock lock = findById(connection, id);

            boolean result;
            if (lock == null) {
                result = tryAcquireInsert(connection, id);
            } else {
                result = tryAcquireUpdate(connection, id, expireMillis);
            }
            return result;
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public boolean release(String id, long expireMillis) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            XkwRocketmqLock lock = findById(connection, id);
            if (lock == null || !Objects.equals(lock.getInstance(), XkwRocketmqConstant.INSTANCE)) {
                return false;
            }

            return tryRelease(connection, id, expireMillis);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public boolean keepalive(String id, long expireMillis) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            return tryKeepalive(connection, id, expireMillis);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    private XkwRocketmqLock findById(Connection connection, String id) {
        PreparedStatement statement = SqlUtil.prepareStatement(connection, LockStatement.GET_BY_ID);
        SqlUtil.setString(statement, 1, id);
        ResultSet resultSet = SqlUtil.executeQuery(statement);
        if (SqlUtil.next(resultSet)) {
            XkwRocketmqLock lock = new XkwRocketmqLock();
            lock.setId(SqlUtil.getString(resultSet, 1));
            lock.setInstance(SqlUtil.getString(resultSet, 2));
            lock.setCreateTime(SqlUtil.getLong(resultSet, 3));
            return lock;
        }
        return null;
    }

    private boolean tryAcquireInsert(Connection connection, String id) {
        PreparedStatement statement = SqlUtil.prepareStatement(connection, LockStatement.ACQUIRE_INSERT);
        SqlUtil.setString(statement, 1, id);
        SqlUtil.setString(statement, 2, XkwRocketmqConstant.INSTANCE);
        SqlUtil.setLong(statement, 3, System.currentTimeMillis());
        try {
            int lines = SqlUtil.executeUpdate(statement);
            return lines > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean tryAcquireUpdate(Connection connection, String id, long expireMillis) {
        PreparedStatement statement = SqlUtil.prepareStatement(connection, LockStatement.ACQUIRE_UPDATE);
        long now = System.currentTimeMillis();
        SqlUtil.setString(statement, 1, XkwRocketmqConstant.INSTANCE);
        SqlUtil.setLong(statement, 2, now);
        SqlUtil.setString(statement, 3, id);
        SqlUtil.setLong(statement, 4, now - expireMillis);
        int lines = SqlUtil.executeUpdate(statement);
        return lines > 0;
    }

    private boolean tryRelease(Connection connection, String id, long expireMillis) {
        PreparedStatement statement = SqlUtil.prepareStatement(connection, LockStatement.RELEASE);
        SqlUtil.setString(statement, 1, id);
        SqlUtil.setString(statement, 2, XkwRocketmqConstant.INSTANCE);
        SqlUtil.setLong(statement, 3, System.currentTimeMillis() - expireMillis);
        int lines = SqlUtil.executeUpdate(statement);
        return lines > 0;
    }

    private boolean tryKeepalive(Connection connection, String id, long expireMillis) {
        PreparedStatement statement = SqlUtil.prepareStatement(connection, LockStatement.KEEPALIVE);
        long now = System.currentTimeMillis();
        SqlUtil.setLong(statement, 1, now);
        SqlUtil.setString(statement, 2, id);
        SqlUtil.setString(statement, 3, XkwRocketmqConstant.INSTANCE);
        SqlUtil.setLong(statement, 4, now - expireMillis);
        int lines = SqlUtil.executeUpdate(statement);
        return lines > 0;
    }
}
