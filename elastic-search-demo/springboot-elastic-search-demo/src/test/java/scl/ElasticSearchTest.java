package scl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import scl.pojo.User;
import scl.sevice.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * @projectName: learn-projects-demo
 * @package: scl
 * @className: ElasticSearchTest
 * @author: sichaolong
 * @description: TODO
 * @date: 2023/9/3 23:37
 * @version: 1.0
 */

@SpringBootTest
public class ElasticSearchTest {

    @Autowired
    private UserService userService;


    @Test
    public void testInsert() {
        User user = new User();
        user.setId("1");
        user.setUsername("张三");
        user.setPassword("zhangsan");
        userService.save(user);

    }

    @Test
    public void testDeleteUserById() {
        User user = new User();
        user.setId("1");
        userService.delete(user);

    }

    @Test
    public void testGetAllUsers() {
        List<User> list = new ArrayList<>();
        Iterable<User> iterable = userService.getAll();
        iterable.forEach(e->list.add((User) e));

    }

}
