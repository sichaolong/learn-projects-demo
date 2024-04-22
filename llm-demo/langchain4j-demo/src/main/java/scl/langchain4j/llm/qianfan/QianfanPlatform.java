package scl.langchain4j.llm.qianfan;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import scl.langchain4j.llm.PlatformInfo;

/**
 * @author sichaolong
 * @createdate 2024/4/19 16:18
 */
@Data
public class QianfanPlatform extends PlatformInfo {

    @JsonProperty("api_key")
    private String apiKey;

    @JsonProperty("secret_key")
    private String secretKey;
}
