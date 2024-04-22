package scl.langchain4j.llm.Azure;

import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.azure.AzureOpenAiStreamingChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.qianfan.QianfanChatModel;
import dev.langchain4j.model.qianfan.QianfanStreamingChatModel;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import scl.langchain4j.config.LLMConfig;
import scl.langchain4j.constants.LLMConstants;
import scl.langchain4j.llm.AbstractPlatformService;

/**
 * @author sichaolong
 * @createdate 2024/4/22 17:53
 */
@Slf4j
@Accessors(chain = true)
public class AzurePlatformService extends AbstractPlatformService<AzurePlatformInfo> {
    public AzurePlatformService(String modelName) {
        super(modelName, LLMConstants.PlatformKey.AZURE, AzurePlatformInfo.class);
    }

    @Override
    public boolean isEnabled() {
        return LLMConfig.PLATFORM_CONFIGS.containsKey(LLMConstants.PlatformKey.AZURE);
    }

    @Override
    protected ChatLanguageModel buildChatLLM() {

        AzurePlatformInfo platform = (AzurePlatformInfo) LLMConfig.PLATFORM_CONFIGS.get(LLMConstants.PlatformKey.AZURE);

        return AzureOpenAiChatModel.builder()
            .apiKey(platform.getAzureApiKey())
            .deploymentName(platform.getDeploymentName())
            .serviceVersion(platform.getApiVersion())
            .temperature(0.1)
            .topP(1.0)
            .maxRetries(1)
            .build();
    }

    @Override
    protected StreamingChatLanguageModel buildStreamingChatLLM() {

        AzurePlatformInfo platform = (AzurePlatformInfo) LLMConfig.PLATFORM_CONFIGS.get(LLMConstants.PlatformKey.AZURE);

        return AzureOpenAiStreamingChatModel.builder()
            .serviceVersion(platform.getApiVersion())
            .apiKey(platform.getAzureApiKey())
            .deploymentName(platform.getDeploymentName())
            .temperature(0.7)
            .topP(1.0)
            .maxRetries(1)
            .build();
    }

    @Override
    protected String parseError(Object error) {
        return null;
    }
}
