package henu.soft.tomato.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import henu.soft.tomato.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author sichaolong
 * @date 2022/11/13 20:46
 */

@Mapper
public interface UserMapper extends BaseMapper<User> {

    List<User> getListByQueryBuilderRule(@Param("ruleSQL") String ruleSQL);
}
