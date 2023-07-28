package scl.config.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;
import scl.config.annotation.LimitType;
import scl.config.annotation.RateLimiter;
import scl.utils.IPUtil;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: sichaolong
 * @Date: 2023/7/27 15:46
 * @Description:
 * 接口限流切面AOP
 */

@Slf4j
@Aspect
@Component
public class RateLimiterAop {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private DefaultRedisScript<Long> getRedisScript;

    @PostConstruct
    public void init() {
        getRedisScript = new DefaultRedisScript<>();
        getRedisScript.setResultType(Long.class);
        getRedisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/rateLimiter.lua")));
        log.info("[分布式限流处理器]脚本加载完成");
    }

    @Around("@annotation(rateLimiter)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, RateLimiter rateLimiter) throws Throwable {
        log.debug("[分布式限流处理器]开始执行限流操作");
        // 限流模块key
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();

        StringBuilder limitKey = new StringBuilder(rateLimiter.key());

        if (rateLimiter.limitType() == LimitType.IP) {
            limitKey.append(IPUtil.getIpAddress());
        }
        // 目标类、方法
        String className = method.getDeclaringClass().getName();
        String name = method.getName();
        limitKey.append("_").append(className).append("_").append(name);
        // 限流阈值
        long limitCount = rateLimiter.limit();
        // 限流超时时间
        long expireTime = rateLimiter.expire();
        log.debug("[分布式限流处理器]参数值为：method={},limitKey={},limitCount={},limitTimeout={}", name, limitKey, limitCount, expireTime);

        // 执行Lua脚本
        List<String> keyList = new ArrayList<>();
        // 设置key值为注解中的值
        keyList.add(limitKey.toString());

        // 调用脚本并执行
        Long result = redisTemplate.execute(getRedisScript, keyList, expireTime,System.currentTimeMillis(), limitCount);
        log.debug("[分布式限流处理器]限流执行结果-result={}", result);
        if (null != result && result >= limitCount) {
            log.debug("由于超过单位时间={}；允许的请求次数={}[触发限流]", expireTime, limitCount);
            // 限流提示语
            String message = rateLimiter.message();
            throw new RuntimeException(message);
        }
        return proceedingJoinPoint.proceed();
    }
}
