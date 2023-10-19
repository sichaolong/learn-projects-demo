-- 2.2.3优化了顺序消息的发送逻辑
-- 建议应用升级前执行
create index `idx_type_hash_key`
    on xkw_producer_message (`type`, `hash_key`);

