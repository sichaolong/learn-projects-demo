DROP TABLE IF EXISTS `xkw_consumer_message`;
DROP TABLE IF EXISTS `xkw_consumer_error_log`;

CREATE TABLE `xkw_consumer_message` (
                                        `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '消费者消息自增id',
                                        `unique_key` varchar(100) DEFAULT '' COMMENT '生产者为消息设置的唯一标识',
                                        `namespace` varchar(20) DEFAULT '' COMMENT '名称空间，用于隔离消息',
                                        `topic` varchar(50) DEFAULT '' COMMENT '主题',
                                        `tag` varchar(50) DEFAULT '' COMMENT '标签，用于过滤消息',
                                        `payload` mediumtext COMMENT '消息内容',
                                        `status` tinyint unsigned DEFAULT '0' COMMENT '消息状态',
                                        `retry_times` tinyint unsigned DEFAULT '0' COMMENT '重新消费次数',
                                        `start_time` bigint DEFAULT '0' COMMENT '开始时间',
                                        `finish_time` bigint DEFAULT '0' COMMENT '结束时间',
                                        PRIMARY KEY (`id`),
                                        UNIQUE KEY `idx_unique_key` (`unique_key`),
                                        KEY `idx_status_start_time` (`status`,`start_time`)
) ENGINE=InnoDB COMMENT='消费者消息表';

CREATE TABLE `xkw_consumer_error_log` (
                                          `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
                                          `message_id` bigint unsigned DEFAULT NULL COMMENT '消息id',
                                          `create_time` bigint DEFAULT NULL COMMENT '异常时间',
                                          `error_log` mediumtext COMMENT '异常日志',
                                          PRIMARY KEY (`id`),
                                          KEY `idx_message_id` (`message_id`)
) ENGINE=InnoDB COMMENT='消费异常表';

CREATE TABLE IF NOT EXISTS `xkw_rocketmq_lock` (
                                                   `id` varchar(25) NOT NULL COMMENT '锁id',
                                                   `instance` varchar(50) DEFAULT '' COMMENT '当前持有锁的节点标识',
                                                   `create_time` bigint DEFAULT '0' COMMENT '获得锁时间，超时后认为锁失效',
                                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='mq锁';

