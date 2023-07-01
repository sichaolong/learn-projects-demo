package scl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import scl.entity.Config;

/**
 * @projectName: learn-projects-demo
 * @package: scl.mapper
 * @className: ConfigMapper
 * @author: sichaolong
 * @description: TODO
 * @date: 2023/6/29 21:00
 * @version: 1.0
 */
@Mapper
public interface ConfigMapper extends BaseMapper<Config> {
}
