package scl.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @projectName: learn-projects-demo
 * @package: scl.entity
 * @className: User
 * @author: sichaolong
 * @description: TODO
 * @date: 2023/6/27 21:36
 * @version: 1.0
 */
@Data
public class User {
    @TableId
    private String uid;
    private String name;
    private Integer age;
    private String email;
}
