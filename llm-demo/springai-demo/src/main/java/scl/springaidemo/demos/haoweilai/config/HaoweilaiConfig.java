package scl.springaidemo.demos.haoweilai.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static scl.springaidemo.demos.haoweilai.config.HaoweilaiConfig.CONFIG_PREFIX;

/**
 * @author sichaolong
 * @createdate 2024/5/9 09:48
 */

@Data
@Configuration
@ConfigurationProperties(CONFIG_PREFIX)
public class HaoweilaiConfig {

    public static final String CONFIG_PREFIX = "spring.ai.haoweilai.mathgpt";

    @Value("${spring.ai.haoweilai.mathgpt.http.baseUrl:https://openai.100tal.com/}")
    public  String httpBaseUrl;

    @Value("${spring.ai.haoweilai.mathgpt.http.path:aitext/multi-70b/multi-70b/http}")
    public  String httpPath;

    @Value("${spring.ai.haoweilai.mathgpt.ws.baseUrl:wss://openai.100tal.com/}")
    public  String wsBaseUrl;

    @Value("${spring.ai.haoweilai.mathgpt.ws.path:aitext/multi-70b/multi-70b/ws}")
    public  String wsPath;

    @Value("${spring.ai.haoweilai.mathgpt.accessKey:}")
    private String accessKey;

    @Value("${spring.ai.haoweilai.mathgpt.secretKey:}")
    private String secretKey;

    /**
     * 学科0-9分别对应：语、数、外、物、化、生、政、
     * 其他。不传时为空字符串，走数学。
     */
    @Value("${spring.ai.haoweilai.mathgpt.subject:''}")
    private String subject;

    /**
     * 表示单次请求多次采样，比如n=50，单次请求最多
     * 采样结果。
     * 默认值0表示走默认配置10次采样。
     * n取值范围：0 < n <= 50
     */
    @Value("${spring.ai.haoweilai.mathgpt.n:1}")
    private Integer n;


    /**
     * 使用多次采样，即n>1时，请设置stream为true
     */
    @Value("${spring.ai.haoweilai.mathgpt.stream:false}")
    private boolean stream;

}
