DROP TABLE IF EXISTS `xkw_producer_message`;

CREATE TABLE `xkw_producer_message` (
                                        `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '生产者消息自增id',
                                        `key_prefix` varchar(30) DEFAULT '' COMMENT 'key前缀',
                                        `namespace` varchar(20) DEFAULT '' COMMENT '名称空间，用于隔离消息',
                                        `topic` varchar(50) DEFAULT '' COMMENT '主题',
                                        `tag` varchar(50) DEFAULT '' COMMENT '标签，用于过滤消息',
                                        `type` tinyint unsigned DEFAULT '0' COMMENT '消息类型',
                                        `hash_key` varchar(50) DEFAULT '' COMMENT '顺序消息根据hashKey进行散列',
                                        `delay_level` tinyint unsigned DEFAULT '0' COMMENT '延迟等级',
                                        `payload` mediumtext COMMENT '消息内容',
                                        `status` tinyint unsigned DEFAULT '0' COMMENT '消息状态',
                                        `retry_times` tinyint unsigned DEFAULT '0' COMMENT '重新发送次数',
                                        `create_time` bigint DEFAULT '0' COMMENT '创建时间',
                                        `commit_time` bigint DEFAULT '0' COMMENT '发送时间',
                                        PRIMARY KEY (`id`),
                                        KEY `idx_status_create_time_retry_times` (`status`,`create_time`,`retry_times`),
                                        KEY `idx_type_hash_key` (`type`, `hash_key`)
) ENGINE=InnoDB COMMENT='生产者消息表';

CREATE TABLE IF NOT EXISTS `xkw_rocketmq_lock` (
                                                   `id` varchar(25) NOT NULL COMMENT '锁id',
                                                   `instance` varchar(50) DEFAULT '' COMMENT '当前持有锁的节点标识',
                                                   `create_time` bigint DEFAULT '0' COMMENT '获得锁时间，超时后认为锁失效',
                                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='mq锁';

