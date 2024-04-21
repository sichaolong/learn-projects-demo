package scl;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import scl.mapper.UserMapper;
import scl.pojo.User;

import java.util.UUID;

@SpringBootTest
class LearnAnalysisMysqlConnectorJvmUseageApplicationTests {

    @Test
    void contextLoads() {
    }


    @Resource
    UserMapper userMapper;

    @Test
    void testGetUserList(){
        System.out.println(userMapper.getAll());
    }

    @Test
    void testInsertUser(){
        User user = new User();
        for (int i = 0; i < 10; i++) {
            user.setId((long) i);
            user.setName(UUID.randomUUID().toString());
            userMapper.insert(user);
        }

    }
}
