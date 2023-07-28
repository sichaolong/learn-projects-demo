package scl.config.annotation;

import java.lang.annotation.*;

/**
 * @Auther: sichaolong
 * @Date: 2023/7/27 15:22
 * @Description:
 * 自定义接口api限流注解
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {
    /**
     * 限流key
     */
    String key() default "rate:limiter:";

    /**
     * 单位时间限制通过请求数
     */
    long limit() default 1;

    /**
     * 过期时间，单位秒
     */
    long expire() default 5;

    /**
     * 限流提示语
     */
    String message() default "访问过于频繁";

    /**
     * 限流类型
     */
    LimitType limitType() default LimitType.DEFAULT;


}
