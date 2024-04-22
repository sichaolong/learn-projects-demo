package com.moyz.adi.common.helper;

import com.moyz.adi.common.interfaces.AbstractLLMService;
import com.moyz.adi.common.util.JsonUtil;
import com.moyz.adi.common.util.LocalCache;
import com.moyz.adi.common.vo.CommonAiPlatformSetting;
import com.moyz.adi.common.vo.LLMModelInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static dev.langchain4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

/**
 * llmService上下文类（策略模式）
 */
@Slf4j
public class LLMContext {
    public static final Map<String, LLMModelInfo> NAME_TO_MODEL = new LinkedHashMap<>();
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
        LLMModelInfo llmModelInfo = new LLMModelInfo();
        llmModelInfo.setModelName(llmServiceKey);
        llmModelInfo.setEnable(llmService.isEnabled());
        llmModelInfo.setLlmService(llmService);
        NAME_TO_MODEL.put(llmServiceKey, llmModelInfo);
    }

    public AbstractLLMService getLLMService() {
        return llmService;
    }

    public static String[] getSupportModels(String settingName) {
        String st = LocalCache.CONFIGS.get(settingName);
        CommonAiPlatformSetting setting = JsonUtil.fromJson(st, CommonAiPlatformSetting.class);
        return setting.getModels();
    }
}
