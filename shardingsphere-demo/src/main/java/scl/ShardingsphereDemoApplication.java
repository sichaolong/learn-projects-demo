package scl;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class ShardingsphereDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShardingsphereDemoApplication.class, args);
    }

}
