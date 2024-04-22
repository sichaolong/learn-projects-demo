package scl.langchain4j.llm;

import com.alibaba.fastjson.JSON;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;
import scl.langchain4j.assistant.Assistant;
import scl.langchain4j.assistant.AssistantWithoutMemory;
import scl.langchain4j.constants.LLMConstants;
import scl.langchain4j.store.CustomChatMemoryStore;

import java.net.Proxy;

/**
 * @author sichaolong
 * @createdate 2024/4/19 14:10
 */
@Slf4j
public abstract class AbstractLLMService<T>{

    protected Proxy proxy;

    protected String modelName;

    protected T platform;

    protected StreamingChatLanguageModel streamingChatLanguageModel;
    protected ChatLanguageModel chatLanguageModel;

    private Assistant chatAssistant;

    private AssistantWithoutMemory chatAssistantWithoutMemory;

    private CustomChatMemoryStore chatMemoryStoreService;

    public AbstractLLMService(String modelName, String settingName, Class<T> clazz) {
        this.modelName = modelName;
        String st = LLMConstants.CONFIGS.get(settingName);
        platform = JSON.parseObject(st, clazz);
    }

    public AbstractLLMService setProxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    /**
     * 检测该service是否可用（不可用的情况通常是没有配置key）
     *
     * @return
     */
    public abstract boolean isEnabled();

    public ChatLanguageModel getChatLLM() {
        if (null != chatLanguageModel) {
            return chatLanguageModel;
        }
        chatLanguageModel = buildChatLLM();
        return chatLanguageModel;
    }

    public StreamingChatLanguageModel getStreamingChatLLM() {
        if (null != streamingChatLanguageModel) {
            return streamingChatLanguageModel;
        }
        streamingChatLanguageModel = buildStreamingChatLLM();
        return streamingChatLanguageModel;
    }

    protected abstract ChatLanguageModel buildChatLLM();

    protected abstract StreamingChatLanguageModel buildStreamingChatLLM();

    protected abstract String parseError(Object error);

    public Response<AiMessage> chat(ChatMessage chatMessage) {
        if (!isEnabled()) {
            log.error("llm service is disabled");
            throw new RuntimeException("llm service is disabled");
        }
        return getChatLLM().generate(chatMessage);
    }

}
