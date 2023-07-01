package scl.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @projectName: learn-projects-demo
 * @package: scl.entity
 * @className: Config
 * @author: sichaolong
 * @description: TODO
 * @date: 2023/6/29 21:00
 * @version: 1.0
 */

@Data
public class Config {

    @TableId
    private String cid;
    private String ckey;
    private String cvalue;
}
