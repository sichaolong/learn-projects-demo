package scl.demo.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: sichaolong
 * @Date: 2023/7/14 09:49
 * @Description:
 * 简单使用Caffeine
 */
public class CaffeineDemo {

    public static void main(String[] args) {
        Cache<String, String> caffeineCache = Caffeine.newBuilder()
                .initialCapacity(5)
                .maximumSize(10)
                .expireAfterWrite(17, TimeUnit.SECONDS)
                .build();

        // 测试
        caffeineCache.put("si","cl");
        System.out.println(caffeineCache.getIfPresent("si"));

        // 测试查询不存在的key，从数据库获取value
        System.out.println(caffeineCache.get("chao",CaffeineDemo::getValueFromDB));



        // 删除key
        caffeineCache.invalidate("si");
        caffeineCache.invalidate("chao");

        // 删除后在获取
        System.out.println(caffeineCache.getIfPresent("si"));
        System.out.println(caffeineCache.get("chao",CaffeineDemo::getValueFromDB));

    }




    /**
     * 模拟查询数据库
     * @return
     */
    private static String getValueFromDB(String key){
        return "dbValue";
    }


}
