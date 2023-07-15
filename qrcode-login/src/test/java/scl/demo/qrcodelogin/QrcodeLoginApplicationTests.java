package scl.demo.qrcodelogin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import scl.demo.qrcodelogin.entity.User;
import scl.demo.qrcodelogin.service.CacheStoreService;
import scl.demo.qrcodelogin.utils.CommonUtil;

@SpringBootTest
class QrcodeLoginApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private CacheStoreService cacheStore;


    /**
     * 模拟移动端首次登录，登录之后才能扫码。
     */
    @Test
    void insertUser() {
        User user = new User();
        user.setUserId("1");
        user.setUserName("John同学");
        user.setAvatar("/avatar.jpg");
        cacheStore.put("user:1", user);
    }

    /**
     * 模拟服务端验证账号密码为移动端生成mobile-token
     */

    @Test
    void loginByPhone() {
        String accessToken = CommonUtil.generateUUID();
        System.out.println(accessToken);
        cacheStore.put(CommonUtil.buildAccessTokenKey(accessToken), "1");
    }

}
