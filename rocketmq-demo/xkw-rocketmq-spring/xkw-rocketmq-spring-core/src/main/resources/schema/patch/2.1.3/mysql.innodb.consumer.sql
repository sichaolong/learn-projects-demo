-- 2.1.3对key的生成规则做了优化
-- 建议应用升级前执行
alter table xkw_consumer_message
    modify unique_key varchar(100) default '' null comment '生产者为消息设置的唯一标识';

