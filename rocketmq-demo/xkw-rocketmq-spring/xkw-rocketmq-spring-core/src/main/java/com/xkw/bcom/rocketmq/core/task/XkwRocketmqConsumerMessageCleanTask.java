/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.task;

import com.xkw.bcom.rocketmq.core.constant.XkwRocketmqConstant;
import com.xkw.bcom.rocketmq.core.message.XkwConsumerMessageStatus;
import com.xkw.bcom.rocketmq.core.repository.XkwRocketmqConsumerRepository;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * XkwRocketmqConsumerMessageCleanTask
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月20日
 */
public class XkwRocketmqConsumerMessageCleanTask extends AbstractXkwRocketmqTask {

    private static final int LIMIT = XkwRocketmqConstant.BATCH_SIZE * 5;

    @Resource
    private XkwRocketmqConsumerRepository consumerRepository;
    @Resource
    private RocketMQProperties rocketMQProperties;

    private int messageOverdueDays;

    public void setMessageOverdueDays(int messageOverdueDays) {
        this.messageOverdueDays = messageOverdueDays;
    }

    /**
     * 每天凌晨2点清理过期消息
     */
    @Override
    @Scheduled(cron = "0 0 2 * * *")
    public void action() {
        super.action();
    }

    @Override
    void mainAction() {
        // 滚动删除过期消息
        long minId = 0;
        long maxCreateTime = System.currentTimeMillis() - messageOverdueDays * 24 * 60 * 60 * 1000L;
        String namespace = rocketMQProperties.getConsumer().getNamespace();
        while (getStatus()) {
            List<Long> ids = consumerRepository.listIdsByStatusAndStartTime(namespace, XkwConsumerMessageStatus.P2, maxCreateTime, minId, LIMIT);
            if (CollectionUtils.isEmpty(ids)) {
                break;
            }
            minId = ids.get(ids.size() - 1);
            consumerRepository.deleteByIds(ids);
        }
    }
}
