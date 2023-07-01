package henu.soft.tomato;

import henu.soft.tomato.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author sichaolong
 * @date 2022/11/13 20:48
 */
@SpringBootTest(classes = TomatoOfLifeQueryBuilderBackApplication.class)
public class UserMapperTest {
    @Resource
    private UserMapper userMapper;

    @Test
    public void selectUser(){
        userMapper.selectList(null).stream().forEach(System.out::println);
    }


}
