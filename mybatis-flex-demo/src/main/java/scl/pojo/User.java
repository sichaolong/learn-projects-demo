package scl.pojo;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

/**
 * @projectName: learn-projects-demo
 * @package: scl.pojo
 * @className: User
 * @author: sichaolong
 * @description: TODO
 * @date: 2023/8/7 21:02
 * @version: 1.0
 */
@Data
@Builder
@Table("user") // mybatis-flex注解
public class User {
    @Id(keyType = KeyType.Auto)
    private Integer id;
    private Integer age;
    private String name;
    private String password;

    @Tolerate
    User() {}
}

