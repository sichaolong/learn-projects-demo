/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.repository.mysql;

import com.xkw.bcom.rocketmq.core.constant.XkwRocketmqConstant;
import com.xkw.bcom.rocketmq.core.message.XkwConsumerMessage;
import com.xkw.bcom.rocketmq.core.message.XkwConsumerMessageStatus;
import com.xkw.bcom.rocketmq.core.repository.XkwRocketmqConsumerRepository;
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
import java.util.function.BiFunction;

/**
 * XkwRocketmqConsumerRepositoryMysqlImpl
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月11日
 */
public class XkwRocketmqConsumerRepositoryMysqlImpl implements XkwRocketmqConsumerRepository {

    @Resource
    private DataSource dataSource;

    @Override
    public Long save(XkwConsumerMessage message) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            PreparedStatement statement = SqlUtil.prepareStatement(connection, ConsumerStatement.INSERT, Statement.RETURN_GENERATED_KEYS);
            SqlUtil.setString(statement, 1, message.getUniqueKey());
            SqlUtil.setString(statement, 2, message.getNamespace(), XkwRocketmqConstant.BLANK);
            SqlUtil.setString(statement, 3, message.getTopic(), XkwRocketmqConstant.BLANK);
            SqlUtil.setString(statement, 4, message.getTag(), XkwRocketmqConstant.BLANK);
            SqlUtil.setString(statement, 5, message.getPayload(), XkwRocketmqConstant.BLANK);
            SqlUtil.setInt(statement, 6, message.getStatus().getCode());
            SqlUtil.setInt(statement, 7, message.getRetryTimes(), 0);
            SqlUtil.setLong(statement, 8, message.getStartTime(), 0L);
            SqlUtil.setLong(statement, 9, message.getFinishTime(), 0L);
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
    public boolean markPendingIfNotSuccess(XkwConsumerMessage message, XkwConsumerMessage condition) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            PreparedStatement statement = SqlUtil.prepareStatement(connection, ConsumerStatement.MARK_PENDING_IF_NOT_SUCCESS);
            SqlUtil.setLong(statement, 1, message.getStartTime());
            SqlUtil.setInt(statement, 2, message.getRetryTimes());
            SqlUtil.setLong(statement, 3, condition.getId());
            SqlUtil.setInt(statement, 4, condition.getRetryTimes());
            int lines = SqlUtil.executeUpdate(statement);
            return lines > 0;
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public boolean markFailure(XkwConsumerMessage message, XkwConsumerMessage condition) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            PreparedStatement statement = SqlUtil.prepareStatement(connection, ConsumerStatement.MARK_FAILURE);
            SqlUtil.setInt(statement, 1, message.getStatus().getCode());
            SqlUtil.setLong(statement, 2, condition.getId());
            SqlUtil.setInt(statement, 3, condition.getStatus().getCode());
            SqlUtil.setInt(statement, 4, condition.getRetryTimes());
            int lines = SqlUtil.executeUpdate(statement);
            return lines > 0;
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public boolean markSuccess(XkwConsumerMessage message, XkwConsumerMessage condition) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            PreparedStatement statement = SqlUtil.prepareStatement(connection, ConsumerStatement.MARK_SUCCESS);
            SqlUtil.setInt(statement, 1, message.getStatus().getCode());
            SqlUtil.setLong(statement, 2, message.getStartTime());
            SqlUtil.setLong(statement, 3, message.getFinishTime());
            SqlUtil.setLong(statement, 4, condition.getId());
            int lines = SqlUtil.executeUpdate(statement);
            return lines > 0;
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public XkwConsumerMessage findByKey(String key) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            PreparedStatement statement = SqlUtil.prepareStatement(connection, ConsumerStatement.FIND_BY_KEY);
            SqlUtil.setString(statement, 1, key);
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
    public List<Long> listIdsByStatusAndStartTime(String namespace, XkwConsumerMessageStatus status, long maxStartTime, long minId, int limit) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            PreparedStatement statement = SqlUtil.prepareStatement(connection, ConsumerStatement.LIST_IDS_BY_STATUS_AND_START_TIME, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            SqlUtil.setLong(statement, 1, minId);
            SqlUtil.setString(statement, 2, namespace, XkwRocketmqConstant.BLANK);
            SqlUtil.setInt(statement, 3, status.getCode());
            SqlUtil.setLong(statement, 4, maxStartTime);
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
                String sql = String.format(ConsumerStatement.DELETE_BY_IDS, placeholder);
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
    public List<XkwConsumerMessage> listByConditions(String namespace, long minId, String topic, String tag, Integer minRetryTimes,
                                                     Long minStartTime, XkwConsumerMessageStatus status, int limit) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            List<BiFunction<PreparedStatement, Integer, Integer>> functions = new ArrayList<>();
            String placeholder = buildDynamicConditionSql(topic, tag, minRetryTimes, minStartTime, functions);
            String sql = String.format(ConsumerStatement.LIST_BY_CONDITIONS, placeholder);
            PreparedStatement statement = SqlUtil.prepareStatement(connection, sql, ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            int index = 1;
            SqlUtil.setLong(statement, index++, minId);
            SqlUtil.setString(statement, index++, namespace);
            for (BiFunction<PreparedStatement, Integer, Integer> function : functions) {
                index = function.apply(statement, index);
            }
            SqlUtil.setInt(statement, index++, status.getCode());
            SqlUtil.setInt(statement, index, limit);
            ResultSet resultSet = SqlUtil.executeQuery(statement);

            int row = SqlUtil.getRow(resultSet);
            if (row == 0) {
                return null;
            }

            List<XkwConsumerMessage> messages = new ArrayList<>(row);
            while (SqlUtil.next(resultSet)) {
                XkwConsumerMessage message = buildMessage(resultSet);
                messages.add(message);
            }

            return messages;
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public int countByConditions(String namespace, String topic, String tag, Integer minRetryTimes, Long minStartTime,
                                 XkwConsumerMessageStatus status) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            List<BiFunction<PreparedStatement, Integer, Integer>> functions = new ArrayList<>();
            String placeholder = buildDynamicConditionSql(topic, tag, minRetryTimes, minStartTime, functions);
            String sql = String.format(ConsumerStatement.COUNT_BY_CONDITIONS, placeholder);
            PreparedStatement statement = SqlUtil.prepareStatement(connection, sql);
            int index = 1;
            SqlUtil.setString(statement, index++, namespace);
            for (BiFunction<PreparedStatement, Integer, Integer> function : functions) {
                index = function.apply(statement, index);
            }
            SqlUtil.setInt(statement, index, status.getCode());
            ResultSet resultSet = SqlUtil.executeQuery(statement);
            if (SqlUtil.next(resultSet)) {
                return SqlUtil.getInt(resultSet, 1);
            } else {
                return 0;
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    private XkwConsumerMessage buildMessage(ResultSet resultSet) {
        XkwConsumerMessage message = new XkwConsumerMessage();
        message.setId(SqlUtil.getLong(resultSet, 1));
        message.setUniqueKey(SqlUtil.getString(resultSet, 2));
        message.setNamespace(SqlUtil.getString(resultSet, 3));
        message.setTopic(SqlUtil.getString(resultSet, 4));
        message.setTag(SqlUtil.getString(resultSet, 5));
        message.setPayload(SqlUtil.getString(resultSet, 6));
        int statusCode = SqlUtil.getInt(resultSet, 7);
        XkwConsumerMessageStatus status = XkwConsumerMessageStatus.fromCode(statusCode);
        message.setStatus(status);
        message.setRetryTimes(SqlUtil.getInt(resultSet, 8));
        message.setStartTime(SqlUtil.getLong(resultSet, 9));
        message.setFinishTime(SqlUtil.getLong(resultSet, 10));
        return message;
    }

    private static String buildDynamicConditionSql(String topic, String tag, Integer minRetryTimes, Long minStartTime,
                                                   List<BiFunction<PreparedStatement, Integer, Integer>> functions) {
        String placeholder = "";
        if (topic != null) {
            placeholder += "AND topic = ? ";
            functions.add((statement, index) -> {
                SqlUtil.setString(statement, index, topic);
                return index + 1;
            });
        }
        if (tag != null) {
            placeholder += "AND tag = ? ";
            functions.add((statement, index) -> {
                SqlUtil.setString(statement, index, tag);
                return index + 1;
            });
        }
        if (minRetryTimes != null) {
            placeholder += "AND retry_times >= ? ";
            functions.add((statement, index) -> {
                SqlUtil.setInt(statement, index, minRetryTimes);
                return index + 1;
            });
        }
        if (minStartTime != null) {
            placeholder += "AND start_time >= ? ";
            functions.add((statement, index) -> {
                SqlUtil.setLong(statement, index, minStartTime);
                return index + 1;
            });
        }
        return placeholder;
    }
}
