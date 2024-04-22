package scl.langchain4j.llm.qianfan;

import lombok.Data;
import scl.langchain4j.llm.PlatformInfo;

/**
 * @author sichaolong
 * @createdate 2024/4/19 16:18
 */
@Data
public class QianfanPlatformInfo extends PlatformInfo {

    private String qianfanApiKey;

    private String qianfanSecretKey;
}
