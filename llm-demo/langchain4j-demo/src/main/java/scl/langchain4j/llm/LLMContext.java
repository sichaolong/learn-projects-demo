package scl.langchain4j.llm;

import lombok.extern.slf4j.Slf4j;
import scl.langchain4j.config.LLMConfig;
import scl.langchain4j.constants.LLMConstants;

import java.util.LinkedHashMap;
import java.util.Map;
import static dev.langchain4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

/**
 * @author sichaolong
 * @createdate 2024/4/19 14:08
 * llmService上下文类（策略模式）
 */
@Slf4j
public class LLMContext {

    /**
     * 存储platformKey到llmService的映射
     */
    public static final Map<String, LLMServiceReference> NAME_TO_SERVICE = new LinkedHashMap<>();
    private AbstractPlatformService llmService;

    public LLMContext() {
        llmService = NAME_TO_SERVICE.get(LLMConstants.PlatformKey.QIANFAN).getLlmService();
    }

    public LLMContext(String modelName) {
        if (null == NAME_TO_SERVICE.get(modelName)) {
            log.warn("︿︿︿ Can not find {}, use the default model GPT_3_5_TURBO ︿︿︿", modelName);
            llmService = NAME_TO_SERVICE.get(GPT_3_5_TURBO).getLlmService();
        } else {
            llmService = NAME_TO_SERVICE.get(modelName).getLlmService();
        }
    }

    public static void addLLMService(String llmServiceKey, AbstractPlatformService llmService) {
        LLMServiceReference reference = new LLMServiceReference();
        reference.setModelName(llmServiceKey);
        reference.setEnable(llmService.isEnabled());
        reference.setLlmService(llmService);
        NAME_TO_SERVICE.put(llmServiceKey, reference);
    }

    public AbstractPlatformService getLLMService() {
        return llmService;
    }

    public static String[] getSupportModels(String platformKey) {
        PlatformInfo platformInfo = (PlatformInfo) LLMConfig.CONFIGS.get(platformKey);
        return platformInfo.getModels();
    }
}
