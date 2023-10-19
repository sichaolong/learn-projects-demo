/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.repository.mysql;

import com.xkw.bcom.rocketmq.core.message.XkwConsumerErrorLog;
import com.xkw.bcom.rocketmq.core.repository.XkwRocketmqConsumerErrorLogRepository;
import com.xkw.bcom.rocketmq.core.util.SqlUtil;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * XkwRocketmqConsumerErrorLogRepositoryMysqlImpl
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月28日
 */
public class XkwRocketmqConsumerErrorLogRepositoryMysqlImpl implements XkwRocketmqConsumerErrorLogRepository {

    @Resource
    private DataSource dataSource;

    @Override
    public Long save(XkwConsumerErrorLog errorLog) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            PreparedStatement statement = SqlUtil.prepareStatement(connection, ConsumerErrorLogStatement.INSERT, Statement.RETURN_GENERATED_KEYS);
            SqlUtil.setLong(statement, 1, errorLog.getMessageId());
            SqlUtil.setLong(statement, 2, errorLog.getCreateTime());
            SqlUtil.setString(statement, 3, errorLog.getErrorLog());
            int lines = SqlUtil.executeUpdate(statement);
            if (lines <= 0) {
                return null;
            }

            ResultSet resultSet = SqlUtil.getGeneratedKeys(statement);
            if (SqlUtil.next(resultSet)) {
                return SqlUtil.getLong(resultSet, 1);
            }
            return null;
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            SqlUtil.executeBatchByIds(ids, (sub, placeholder) -> {
                String sql = String.format(ConsumerErrorLogStatement.DELETE_BY_IDS, placeholder);
                PreparedStatement statement = SqlUtil.prepareStatement(connection, sql);
                for (int i = 0; i < sub.size(); i++) {
                    SqlUtil.setLong(statement, i + 1, sub.get(i));
                }
                SqlUtil.executeUpdate(statement);
            });
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public List<Long> listIdsByMessageId(long messageId) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            PreparedStatement statement = SqlUtil.prepareStatement(connection, ConsumerErrorLogStatement.LIST_IDS_BY_MESSAGE_ID,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            SqlUtil.setLong(statement, 1, messageId);
            ResultSet resultSet = SqlUtil.executeQuery(statement);
            int row = SqlUtil.getRow(resultSet);
            List<Long> ids = new ArrayList<>(row);
            while (SqlUtil.next(resultSet)) {
                long id = SqlUtil.getLong(resultSet, 1);
                ids.add(id);
            }
            return ids;
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public List<Long> listIdsByMessageIds(List<Long> messageIds) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            List<Long> ids = new ArrayList<>();

            SqlUtil.executeBatchByIds(messageIds, (sub, placeholder) -> {
                String sql = String.format(ConsumerErrorLogStatement.LIST_IDS_BY_MESSAGE_IDS, placeholder);
                PreparedStatement statement = SqlUtil.prepareStatement(connection, sql);
                for (int i = 0; i < sub.size(); i++) {
                    SqlUtil.setLong(statement, i + 1, sub.get(i));
                }
                ResultSet resultSet = SqlUtil.executeQuery(statement);
                while (SqlUtil.next(resultSet)) {
                    long id = SqlUtil.getLong(resultSet, 1);
                    ids.add(id);
                }
            });

            return ids;
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public List<XkwConsumerErrorLog> listByMessageIds(List<Long> messageIds) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            List<XkwConsumerErrorLog> errorLogs = new ArrayList<>();

            SqlUtil.executeBatchByIds(messageIds, (sub, placeholder) -> {
                String sql = String.format(ConsumerErrorLogStatement.LIST_BY_MESSAGE_IDS, placeholder);
                PreparedStatement statement = SqlUtil.prepareStatement(connection, sql);
                for (int i = 0; i < sub.size(); i++) {
                    SqlUtil.setLong(statement, i + 1, sub.get(i));
                }
                ResultSet resultSet = SqlUtil.executeQuery(statement);
                while (SqlUtil.next(resultSet)) {
                    long id = SqlUtil.getLong(resultSet, 1);
                    long messageId = SqlUtil.getLong(resultSet, 2);
                    long createTime = SqlUtil.getLong(resultSet, 3);
                    String log = SqlUtil.getString(resultSet, 4);
                    XkwConsumerErrorLog errorLog = new XkwConsumerErrorLog(id, messageId, createTime, log);
                    errorLogs.add(errorLog);
                }
            });

            return errorLogs;
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }
}
