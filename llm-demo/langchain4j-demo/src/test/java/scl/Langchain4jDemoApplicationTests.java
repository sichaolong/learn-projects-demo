package scl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import scl.mapper.UserMapper;

@SpringBootTest
class Langchain4jDemoApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    UserMapper userMapper;

    @Test
    public void testUserMapper() {
        System.out.println(userMapper.selectList(null));
    }

}
