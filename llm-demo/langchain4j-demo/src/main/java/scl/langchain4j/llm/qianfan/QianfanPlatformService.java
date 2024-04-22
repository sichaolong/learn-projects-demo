package scl.langchain4j.llm.qianfan;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.qianfan.QianfanChatModel;
import dev.langchain4j.model.qianfan.QianfanStreamingChatModel;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import scl.langchain4j.config.LLMConfig;
import scl.langchain4j.constants.LLMConstants;
import scl.langchain4j.llm.AbstractPlatformService;

/**
 * @author sichaolong
 * @createdate 2024/4/19 16:19
 */

@Slf4j
@Accessors(chain = true)
public class QianfanPlatformService extends AbstractPlatformService<QianfanPlatformInfo> {

    public QianfanPlatformService(String modelName) {
        super(modelName, LLMConstants.PlatformKey.QIANFAN, QianfanPlatformInfo.class);
    }

    @Override
    public boolean isEnabled() {
        return LLMConfig.PLATFORM_CONFIGS.containsKey(LLMConstants.PlatformKey.QIANFAN);
    }

    @Override
    protected ChatLanguageModel buildChatLLM() {

        QianfanPlatformInfo platform = (QianfanPlatformInfo) LLMConfig.PLATFORM_CONFIGS.get(LLMConstants.PlatformKey.QIANFAN);
        return QianfanChatModel.builder()
            .modelName(modelName)
            .temperature(0.1)
            .topP(1.0)
            .maxRetries(1)
            .apiKey(platform.getQianfanApiKey())
            .secretKey(platform.getQianfanSecretKey())
            .endpoint(LLMConstants.ModelParamKey.QIANFAN_ENDPOINT_COMPLETIONS)
            .build();
    }

    @Override
    protected StreamingChatLanguageModel buildStreamingChatLLM() {
        return QianfanStreamingChatModel.builder()
            .modelName(modelName)
            .temperature(0.7)
            .topP(1.0)
            .apiKey(platform.getQianfanApiKey())
            .secretKey(platform.getQianfanSecretKey())
            .build();
    }

    @Override
    protected String parseError(Object error) {
        return null;
    }
}
