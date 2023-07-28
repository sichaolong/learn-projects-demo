package scl.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scl.config.annotation.LimitType;
import scl.config.annotation.RateLimiter;

/**
 * @Auther: sichaolong
 * @Date: 2023/7/28 08:49
 * @Description:
 */

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("")
    public String hello(){
        return "hello";
    }
    @GetMapping("/test")
    public String test(){
        return "test";
    }

    /**
     * 测试限流注解
     * @return
     */
    @RateLimiter(limit = 5,expire = 5,limitType = LimitType.IP)
    @GetMapping("/testRateLimit1")
    public String testRateLimit1(){
        return "testRateLimit1";
    }

    /**
     * 测试限流注解
     * @return
     */
    @RateLimiter(limit = 5,expire = 5,limitType = LimitType.DEFAULT)
    @GetMapping("/testRateLimit2")
    public String testRateLimit2(){
        return "testRateLimit2";
    }

}
