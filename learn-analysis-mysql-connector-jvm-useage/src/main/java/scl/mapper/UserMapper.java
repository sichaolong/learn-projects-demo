package scl.mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import scl.pojo.User;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user")
    List<User> getAll();

    @Insert("INSERT INTO user(id,name) VALUES(null,#{name})")
    void insert(User user);
}
