package scl.mybatis.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import scl.pojo.User;

import java.util.List;

/**
 * @projectName: learn-projects-demo
 * @package: scl.mapper
 * @className: UserMapper
 * @author: sichaolong
 * @description: TODO
 * @date: 2023/8/7 21:03
 * @version: 1.0
 */
public interface MybatisUserMapper {
    @Select("SELECT * FROM user")
    List<User> getAll();

    @Select("SELECT * FROM user WHERE id = #{id}")
    User getOne(Integer id);

    @Insert("INSERT INTO user(name,password,age) VALUES(#{name}, #{password}, #{age})")
    void insert(User user);

    @Update("UPDATE user SET name=#{name},password=#{password},age=#{age} WHERE id =#{id}")
    void update(User user);

    @Delete("DELETE FROM user WHERE id =#{id}")
    void delete(Integer id);
}
