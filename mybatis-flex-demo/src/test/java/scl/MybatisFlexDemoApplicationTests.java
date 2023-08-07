package scl;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import scl.mybatis.mapper.MybatisUserMapper;
import scl.mybatis_flex.mapper.MybatisFlexUserMapper;
//import scl.mybatis_plus.mapper.MybatisPlusUserMapper;
import scl.pojo.User;

import java.util.List;

import static scl.pojo.table.UserTableDef.USER;

@SpringBootTest
@Slf4j
class MybatisFlexDemoApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private MybatisUserMapper mybatisUserMapper;

    @Test
    void testInsert() {
        mybatisUserMapper.insert(User.builder().age(18).name("沉默王二").password("123456").build());
        mybatisUserMapper.insert(User.builder().age(18).name("沉默王三").password("123456").build());
        mybatisUserMapper.insert(User.builder().age(18).name("沉默王四").password("123456").build());
        log.info("查询所有：{}", mybatisUserMapper.getAll().stream());
    }

    @Test
    void testQuery() {
        List<User> all = mybatisUserMapper.getAll();
        log.info("查询所有：{}",all);
    }

    @Test
    void testUpdate() {
        User one = mybatisUserMapper.getOne(1);
        log.info("更新前{}", one);
        one.setPassword("654321");
        mybatisUserMapper.update(one);
        log.info("更新后{}", mybatisUserMapper.getOne(1));
    }

    @Test
    void testDelete() {
        log.info("删除前{}", mybatisUserMapper.getAll());
        mybatisUserMapper.delete(1);
        log.info("删除后{}", mybatisUserMapper.getAll());

    }


//    @Autowired
//    MybatisPlusUserMapper mybatisPlusUserMapper;

//    @Test
//    void testMybatisPlus(){
//        List<User> users = mybatisPlusUserMapper.selectList(null);
//        System.out.println(mybatisPlusUserMapper.selectById(1));
//        System.out.println(users);
//    }



    @Autowired
    MybatisFlexUserMapper mybatisFlexUserMapper;

    @Test
    void testMybatisFlex(){
        List<User> users = mybatisFlexUserMapper.selectAll();
         System.out.println(mybatisFlexUserMapper.selectOneById(2));
        // 和mybatis-plus冲突报错
        System.out.println(users);
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .where(USER.ID.eq(1));
        User user = mybatisFlexUserMapper.selectOneByQuery(queryWrapper);
        System.out.println(user);
    }

}
