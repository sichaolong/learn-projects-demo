--获取KEY
local key = KEYS[1]
--获取ARGV内的参数

-- 缓存时间
local expire = tonumber(ARGV[1])
-- 当前时间
local currentMs = tonumber(ARGV[2])
-- 最大次数
local count = tonumber(ARGV[3])
--窗口开始时间
local windowStartMs = currentMs - expire * 1000;
--获取key的次数
local current = redis.call('zcount', key, windowStartMs, currentMs)

--如果key的次数存在且大于预设值直接返回当前key的次数
if current and tonumber(current) >= count then
    return tonumber(current);
end

-- 清除所有过期成员
redis.call("ZREMRANGEBYSCORE", key, 0, windowStartMs);
-- 添加当前成员
redis.call("zadd", key, tostring(currentMs), currentMs);
redis.call("expire", key, expire);

--返回key的次数
return tonumber(current)
