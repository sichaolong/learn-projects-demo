/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.repository.mysql;

import com.xkw.bcom.rocketmq.core.constant.XkwRocketmqConstant;
import com.xkw.bcom.rocketmq.core.message.XkwMessageType;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessage;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessageBuilder;
import com.xkw.bcom.rocketmq.core.message.XkwProducerMessageStatus;
import com.xkw.bcom.rocketmq.core.repository.XkwRocketmqProducerRepository;
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
 * XkwRocketmqProducerRepositoryMysqlImpl
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月11日
 */
public class XkwRocketmqProducerRepositoryMysqlImpl implements XkwRocketmqProducerRepository {

    @Resource
    private DataSource dataSource;

    @Override
    public Long save(XkwProducerMessage message) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            PreparedStatement statement = SqlUtil.prepareStatement(connection, ProducerStatement.INSERT, Statement.RETURN_GENERATED_KEYS);
            preparedStatementSetValue(statement, message);
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
    public List<Long> saveBatch(List<XkwProducerMessage> messages) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            List<Long> ids = new ArrayList<>(messages.size());
            PreparedStatement statement = SqlUtil.prepareStatement(connection, ProducerStatement.INSERT, Statement.RETURN_GENERATED_KEYS);

            int i = 0;
            for (XkwProducerMessage message : messages) {
                preparedStatementSetValue(statement, message);
                SqlUtil.addBatch(statement);
                i++;
                if (i % XkwRocketmqConstant.BATCH_SIZE == 0) {
                    executeBatch(statement, ids);
                }
            }

            executeBatch(statement, ids);
            return ids;
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    private void executeBatch(PreparedStatement statement, List<Long> ids) {
        SqlUtil.executeBatch(statement);
        SqlUtil.clearBatch(statement);
        ResultSet resultSet = SqlUtil.getGeneratedKeys(statement);
        while (SqlUtil.next(resultSet)) {
            long id = SqlUtil.getLong(resultSet, 1);
            ids.add(id);
        }
    }

    @Override
    public List<XkwProducerMessage> listByIds(List<Long> ids) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            List<XkwProducerMessage> messages = new ArrayList<>(ids.size());

            SqlUtil.executeBatchByIds(ids, (sub, placeholder) -> {
                String sql = String.format(ProducerStatement.LIST_BY_IDS, placeholder);
                PreparedStatement statement = SqlUtil.prepareStatement(connection, sql);
                for (int k = 0; k < sub.size(); k++) {
                    SqlUtil.setLong(statement, k + 1, sub.get(k));
                }
                ResultSet resultSet = SqlUtil.executeQuery(statement);
                while (SqlUtil.next(resultSet)) {
                    XkwProducerMessage message = buildMessage(resultSet);
                    messages.add(message);
                }
            });

            return messages;
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public void updateStatusByIds(List<Long> ids, XkwProducerMessageStatus status) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            SqlUtil.executeBatchByIds(ids, (sub, placeholder) -> {
                String sql = String.format(ProducerStatement.UPDATE_STATUS_BY_IDS, placeholder);
                PreparedStatement statement = SqlUtil.prepareStatement(connection, sql);
                SqlUtil.setInt(statement, 1, status.getCode());
                for (int k = 0; k < sub.size(); k++) {
                    SqlUtil.setLong(statement, k + 2, sub.get(k));
                }
                SqlUtil.executeUpdate(statement);
            });
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public void updateStatusAndCommitTimeByIds(List<Long> ids, XkwProducerMessageStatus status, long commitTime) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            SqlUtil.executeBatchByIds(ids, (sub, placeholder) -> {
                String sql = String.format(ProducerStatement.UPDATE_STATUS_AND_COMMIT_TIME_BY_IDS, placeholder);
                PreparedStatement statement = SqlUtil.prepareStatement(connection, sql);
                SqlUtil.setInt(statement, 1, status.getCode());
                SqlUtil.setLong(statement, 2, commitTime);
                for (int k = 0; k < sub.size(); k++) {
                    SqlUtil.setLong(statement, k + 3, sub.get(k));
                }
                SqlUtil.executeUpdate(statement);
            });
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public void updateStatusAndIncreaseRetryTimesByIds(List<Long> ids, XkwProducerMessageStatus status) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            SqlUtil.executeBatchByIds(ids, (sub, placeholder) -> {
                String sql = String.format(ProducerStatement.UPDATE_STATUS_AND_INCREASE_RETRY_TIMES_BY_IDS, placeholder);
                PreparedStatement statement = SqlUtil.prepareStatement(connection, sql);
                SqlUtil.setInt(statement, 1, status.getCode());
                for (int k = 0; k < sub.size(); k++) {
                    SqlUtil.setLong(statement, k + 2, sub.get(k));
                }
                SqlUtil.executeUpdate(statement);
            });
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public List<Long> listIdsByStatusAndCreateTime(String namespace, XkwProducerMessageStatus status, long maxCreateTime, long minId, int limit) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            PreparedStatement statement = SqlUtil.prepareStatement(connection, ProducerStatement.LIST_IDS_BY_STATUS_AND_CREATE_TIME, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            SqlUtil.setLong(statement, 1, minId);
            SqlUtil.setString(statement, 2, namespace, XkwRocketmqConstant.BLANK);
            SqlUtil.setInt(statement, 3, status.getCode());
            SqlUtil.setLong(statement, 4, maxCreateTime);
            SqlUtil.setInt(statement, 5, limit);
            ResultSet resultSet = SqlUtil.executeQuery(statement);

            int row = SqlUtil.getRow(resultSet);
            if (row == 0) {
                return null;
            }

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
    public void deleteByIds(List<Long> ids) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            SqlUtil.executeBatchByIds(ids, (sub, placeholder) -> {
                String sql = String.format(ProducerStatement.DELETE_BY_IDS, placeholder);
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
    public List<XkwProducerMessage> listUnsentMessages(String namespace, long maxCreateTime, long minId, int maxRetryTimes, int limit) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            PreparedStatement statement = SqlUtil.prepareStatement(connection, ProducerStatement.LIST_UNSENT, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            SqlUtil.setLong(statement, 1, minId);
            SqlUtil.setString(statement, 2, namespace, XkwRocketmqConstant.BLANK);
            SqlUtil.setInt(statement, 3, maxRetryTimes);
            SqlUtil.setLong(statement, 4, maxCreateTime);
            SqlUtil.setInt(statement, 5, limit);
            ResultSet resultSet = SqlUtil.executeQuery(statement);

            int row = SqlUtil.getRow(resultSet);
            if (row == 0) {
                return null;
            }

            List<XkwProducerMessage> messages = new ArrayList<>(row);
            while (SqlUtil.next(resultSet)) {
                XkwProducerMessage message = buildMessage(resultSet);
                messages.add(message);
            }

            return messages;
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public XkwProducerMessage findFirstUnsentMessageByHashKey(String namespace, XkwMessageType type, String hashKey, int maxRetryTimes) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            PreparedStatement statement = SqlUtil.prepareStatement(connection, ProducerStatement.FIND_FIRST_UNSENT_BY_HASH_KEY);
            SqlUtil.setString(statement, 1, namespace);
            SqlUtil.setInt(statement, 2, type.getCode());
            SqlUtil.setString(statement, 3, hashKey);
            SqlUtil.setInt(statement, 4, maxRetryTimes);

            ResultSet resultSet = SqlUtil.executeQuery(statement);
            if (SqlUtil.next(resultSet)) {
                return buildMessage(resultSet);
            }
            return null;
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public List<XkwProducerMessage> listByMinIdAndStatusAndRetryTimes(String namespace, long minId, XkwProducerMessageStatus status, int maxRetryTimes, int limit) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            PreparedStatement statement = SqlUtil.prepareStatement(connection, ProducerStatement.LIST_BY_MIN_ID_AND_STATUS_AND_RETRY_TIMES, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            SqlUtil.setLong(statement, 1, minId);
            SqlUtil.setString(statement, 2, namespace);
            SqlUtil.setInt(statement, 3, status.getCode());
            SqlUtil.setInt(statement, 4, maxRetryTimes);
            SqlUtil.setInt(statement, 5, limit);
            ResultSet resultSet = SqlUtil.executeQuery(statement);

            int row = SqlUtil.getRow(resultSet);
            if (row == 0) {
                return null;
            }

            List<XkwProducerMessage> messages = new ArrayList<>(row);
            while (SqlUtil.next(resultSet)) {
                XkwProducerMessage message = buildMessage(resultSet);
                messages.add(message);
            }

            return messages;
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    private void preparedStatementSetValue(PreparedStatement statement, XkwProducerMessage message) {
        SqlUtil.setString(statement, 1, message.getKeyPrefix(), XkwRocketmqConstant.BLANK);
        SqlUtil.setString(statement, 2, message.getNamespace(), XkwRocketmqConstant.BLANK);
        SqlUtil.setString(statement, 3, message.getTopic(), XkwRocketmqConstant.BLANK);
        SqlUtil.setString(statement, 4, message.getTag(), XkwRocketmqConstant.BLANK);
        SqlUtil.setInt(statement, 5, message.getType().getCode(), 0);
        SqlUtil.setString(statement, 6, message.getHashKey(), XkwRocketmqConstant.BLANK);
        SqlUtil.setInt(statement, 7, message.getDelayLevel(), 0);
        SqlUtil.setString(statement, 8, message.getPayload(), XkwRocketmqConstant.BLANK);
        SqlUtil.setInt(statement, 9, message.getStatus().getCode(), 0);
        SqlUtil.setInt(statement, 10, message.getRetryTimes(), 0);
        SqlUtil.setLong(statement, 11, message.getCreateTime(), 0L);
        SqlUtil.setLong(statement, 12, message.getCommitTime(), 0L);
    }

    private XkwProducerMessage buildMessage(ResultSet resultSet) {
        XkwProducerMessage message = new XkwProducerMessageBuilder().build();
        message.setId(SqlUtil.getLong(resultSet, 1));
        message.setKeyPrefix(SqlUtil.getString(resultSet, 2));
        message.setNamespace(SqlUtil.getString(resultSet, 3));
        message.setTopic(SqlUtil.getString(resultSet, 4));
        message.setTag(SqlUtil.getString(resultSet, 5));
        int typeCode = SqlUtil.getInt(resultSet, 6);
        XkwMessageType type = XkwMessageType.fromCode(typeCode);
        message.setType(type);
        message.setHashKey(SqlUtil.getString(resultSet, 7));
        message.setDelayLevel(SqlUtil.getInt(resultSet, 8));
        message.setPayload(SqlUtil.getString(resultSet, 9));
        int statusCode = SqlUtil.getInt(resultSet, 10);
        XkwProducerMessageStatus status = XkwProducerMessageStatus.fromCode(statusCode);
        message.setStatus(status);
        message.setRetryTimes(SqlUtil.getInt(resultSet, 11));
        message.setCreateTime(SqlUtil.getLong(resultSet, 12));
        message.setCommitTime(SqlUtil.getLong(resultSet, 13));
        return message;
    }
}
