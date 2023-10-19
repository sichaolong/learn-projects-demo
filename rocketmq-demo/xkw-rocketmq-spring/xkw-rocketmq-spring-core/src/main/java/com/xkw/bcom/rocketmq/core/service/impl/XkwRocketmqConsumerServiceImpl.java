/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.service.impl;

import com.xkw.bcom.rocketmq.core.constant.XkwRocketmqConstant;
import com.xkw.bcom.rocketmq.core.message.*;
import com.xkw.bcom.rocketmq.core.repository.XkwRocketmqConsumerErrorLogRepository;
import com.xkw.bcom.rocketmq.core.repository.XkwRocketmqConsumerRepository;
import com.xkw.bcom.rocketmq.core.service.XkwRocketmqConsumerService;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * XkwRocketmqConsumerServiceImpl
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月25日
 */
public class XkwRocketmqConsumerServiceImpl implements XkwRocketmqConsumerService {

    private static final XkwConsumerErrorLogVO EMPTY_ERROR_LOG = new XkwConsumerErrorLogVO();

    @Resource
    private PlatformTransactionManager transactionManager;
    @Resource
    private XkwRocketmqConsumerRepository consumerRepository;
    @Resource
    private XkwRocketmqConsumerErrorLogRepository consumerErrorLogRepository;

    private String namespace;

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public XkwConsumerErrorMessageVO getUnfinishedMessageByUniqueKey(String uniqueKey) {
        XkwConsumerMessage message = consumerRepository.findByKey(uniqueKey);
        if (message == null) {
            return null;
        }
        if (message.getStatus() != XkwConsumerMessageStatus.P1) {
            return null;
        }

        XkwConsumerErrorMessageVO errorMessageVO = new XkwConsumerErrorMessageVO();
        BeanUtils.copyProperties(message, errorMessageVO);
        errorMessageVO.setId(String.valueOf(message.getId()));

        List<Long> messageIds = Collections.singletonList(message.getId());
        List<XkwConsumerErrorLog> errorLogs = consumerErrorLogRepository.listByMessageIds(messageIds);

        XkwConsumerErrorLogVO errorLogVO;
        if (!CollectionUtils.isEmpty(errorLogs)) {
            errorLogVO = buildErrorLogVO(errorLogs.get(0));
        } else {
            errorLogVO = EMPTY_ERROR_LOG;
        }
        errorMessageVO.setErrorLog(errorLogVO);
        return errorMessageVO;
    }

    @Override
    public List<XkwConsumerErrorMessageVO> listUnfinishedMessages(long minId, String topic, String tag, Integer minRetryTimes,
                                                                  Long minStartTime, int limit) {
        String namespace = this.namespace == null ? "" : this.namespace;
        List<XkwConsumerMessage> messages = consumerRepository.listByConditions(namespace, minId, topic, tag,
                minRetryTimes, minStartTime, XkwConsumerMessageStatus.P1, limit);
        if (CollectionUtils.isEmpty(messages)) {
            return null;
        }
        int size = messages.size();
        List<Long> messageIds = messages.stream()
                .map(XkwConsumerMessage::getId)
                .collect(XkwRocketmqConstant.toList(size));
        List<XkwConsumerErrorLog> errorLogs = consumerErrorLogRepository.listByMessageIds(messageIds);
        return messages.stream()
                .map(message -> {
                    XkwConsumerErrorMessageVO errorMessageVO = new XkwConsumerErrorMessageVO();
                    BeanUtils.copyProperties(message, errorMessageVO);
                    errorMessageVO.setId(String.valueOf(message.getId()));
                    errorLogs.stream()
                            .filter(errorLog -> Objects.equals(message.getId(), errorLog.getMessageId()))
                            .findFirst()
                            .map(this::buildErrorLogVO)
                            .ifPresent(errorMessageVO::setErrorLog);

                    if (errorMessageVO.getErrorLog() == null) {
                        // 脏数据，设置一个空的异常信息
                        errorMessageVO.setErrorLog(EMPTY_ERROR_LOG);
                    }
                    return errorMessageVO;
                }).collect(XkwRocketmqConstant.toList(size));
    }

    @Override
    public int countUnfinishedMessages(String topic, String tag, Integer minRetryTimes, Long minStartTime) {
        String namespace = this.namespace == null ? "" : this.namespace;
        return consumerRepository.countByConditions(namespace, topic, tag, minRetryTimes, minStartTime, XkwConsumerMessageStatus.P1);
    }

    @Override
    public void deleteMessages(List<Long> ids) {
        List<Long> errorLogIds = consumerErrorLogRepository.listIdsByMessageIds(ids);
        TransactionStatus status = null;
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            status = transactionManager.getTransaction(XkwRocketmqConstant.TRANSACTION_DEFINITION);
        }
        consumerRepository.deleteByIds(ids);
        if (!CollectionUtils.isEmpty(errorLogIds)) {
            consumerErrorLogRepository.deleteByIds(errorLogIds);
        }
        if (status != null) {
            transactionManager.commit(status);
        }
    }

    private XkwConsumerErrorLogVO buildErrorLogVO(XkwConsumerErrorLog errorLog) {
        XkwConsumerErrorLogVO errorLogVO = new XkwConsumerErrorLogVO();
        errorLogVO.setId(String.valueOf(errorLog.getId()));
        errorLogVO.setMessageId(String.valueOf(errorLog.getMessageId()));
        errorLogVO.setCreateTime(errorLog.getCreateTime());
        errorLogVO.setErrorLog(errorLog.getErrorLog());
        return errorLogVO;
    }
}
