package scl.milvus;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sichaolong
 * @createdate 2024/4/17 15:51
 */
@Configuration
public class MilvusConfig {

    @Value("${milvus.host}")
    private String host; //milvus所在服务器地址
    @Value("${milvus.port}")
    private Integer port; //milvus端口

    @Bean
    public MilvusServiceClient milvusServiceClient() {
        ConnectParam connectParam = ConnectParam.newBuilder()
            .withHost(host)
            .withPort(port)
            .build();
        return new MilvusServiceClient(connectParam);
    }
}