package scl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import scl.mapper.UserMapper;
import scl.milvus.MilvusService;

/**
 * @author sichaolong
 * @createdate 2024/4/17 16:53
 */
@SpringBootTest
public class UserMapperTest {

    @Autowired
    UserMapper userMapper;

    @Test
    public void testUserMapper() {
        System.out.println(userMapper.selectList(null));
    }
}
