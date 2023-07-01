package scl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import scl.entity.Config;
import scl.entity.User;
import scl.mapper.ConfigMapper;
import scl.mapper.UserMapper;
import scl.service.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * @projectName: learn-projects-demo
 * @package: scl
 * @className: SimpleTest
 * @author: sichaolong
 * @description: TODO
 * @date: 2023/6/27 21:37
 * @version: 1.0
 */
@SpringBootTest(classes = ShardingsphereDemoApplication.class)
public class SimpleTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private UserService userService;

    @Test
    public void testSelectUsers() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userMapper.selectList(null);
        userList.forEach(System.out::println);
        System.out.println(userList.size());
    }

    @Test
    public void testInsertUsers() {
        List<User> userList = new ArrayList<>();
        for (int i = 2000; i < 2050; i++) {
            User user =  new User();
            user.setUid(IdWorker.get32UUID().toLowerCase());
            user.setAge(i);
            user.setName("master_slave_sharding" + i);
            user.setEmail(i + "@qq.com");
            userList.add(user);
        }
        userService.saveBatch(userList);
    }

    @Test
    public void testSelectConfigs() {
        System.out.println(("----- selectAll method test ------"));
        List<Config> configList = configMapper.selectList(null);
        configList.forEach(System.out::println);
        System.out.println(configList.size());
    }

    @Test
    public void testInsertConfig() {
        for (int i = 1; i < 10; i++) {
            Config config = new Config();
            config.setCid(IdWorker.get32UUID().toLowerCase());
            config.setCkey("textKey" + i);
            config.setCvalue("testValue" + i);
            configMapper.insert(config);
        }

    }

}
