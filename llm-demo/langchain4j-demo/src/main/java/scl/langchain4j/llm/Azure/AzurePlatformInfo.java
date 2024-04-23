package scl.langchain4j.llm.Azure;

import lombok.Data;
import scl.langchain4j.llm.PlatformInfo;

/**
 * @author sichaolong
 * @createdate 2024/4/22 17:53
 */
@Data
public class AzurePlatformInfo extends PlatformInfo {


    private String azureApiKey;

    private String deploymentName;

    private String apiVersion;

    private String endpoint;


}
