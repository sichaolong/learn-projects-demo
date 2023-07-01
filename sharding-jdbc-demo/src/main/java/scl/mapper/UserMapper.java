package scl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import scl.entity.User;

/**
 * @projectName: learn-projects-demo
 * @package: scl.mapper
 * @className: UserMapper
 * @author: sichaolong
 * @description: TODO
 * @date: 2023/6/27 21:37
 * @version: 1.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}