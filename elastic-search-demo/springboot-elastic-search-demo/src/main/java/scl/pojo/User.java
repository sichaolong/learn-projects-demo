package scl.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;

/**
 * @projectName: learn-projects-demo
 * @package: scl.pojo
 * @className: User
 * @author: sichaolong
 * @description: TODO
 * @date: 2023/9/3 23:33
 * @version: 1.0
 */
@Data
@Document(indexName = "user")
public class User implements Serializable {
    @Id
    private String id; // id
    private String username; // 用户名
    private String password; // 密码
}