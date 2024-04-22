package scl.langchain4j.llm;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
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
    public static final Map<String, ModelInfo> NAME_TO_MODEL = new LinkedHashMap<>();
    private AbstractLLMService llmService;

    public LLMContext() {
        llmService = NAME_TO_MODEL.get(GPT_3_5_TURBO).getLlmService();
    }

    public LLMContext(String modelName) {
        if (null == NAME_TO_MODEL.get(modelName)) {
            log.warn("︿︿︿ Can not find {}, use the default model GPT_3_5_TURBO ︿︿︿", modelName);
            llmService = NAME_TO_MODEL.get(GPT_3_5_TURBO).getLlmService();
        } else {
            llmService = NAME_TO_MODEL.get(modelName).getLlmService();
        }
    }

    public static void addLLMService(String llmServiceKey, AbstractLLMService llmService) {
        ModelInfo llmModelInfo = new ModelInfo();
        llmModelInfo.setModelName(llmServiceKey);
        llmModelInfo.setEnable(llmService.isEnabled());
        llmModelInfo.setLlmService(llmService);
        NAME_TO_MODEL.put(llmServiceKey, llmModelInfo);
    }

    public AbstractLLMService getLLMService() {
        return llmService;
    }

    public static String[] getSupportModels(String settingName) {
        String st = LLMConstants.CONFIGS.get(settingName);
        PlatformInfo platform = JSON.parseObject(st, PlatformInfo.class);
        return platform.getModels();
    }
}
