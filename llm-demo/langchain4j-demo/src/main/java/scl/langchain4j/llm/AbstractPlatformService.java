package scl.langchain4j.llm;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import scl.langchain4j.assistant.Assistant;
import scl.langchain4j.assistant.AssistantWithoutMemory;
import scl.langchain4j.config.LLMConfig;
import scl.langchain4j.store.CustomChatMemoryStore;

import java.net.Proxy;
import java.util.List;

/**
 * @author sichaolong
 * @createdate 2024/4/19 14:10
 */
@Slf4j
public abstract class AbstractPlatformService<T>{

    protected Proxy proxy;

    protected String modelName;

    protected T platform;

    protected StreamingChatLanguageModel streamingChatLanguageModel;
    protected ChatLanguageModel chatLanguageModel;

    private Assistant chatAssistant;

    private AssistantWithoutMemory chatAssistantWithoutMemory;

    private CustomChatMemoryStore chatMemoryStoreService;

    public AbstractPlatformService(String modelName, String platformKey, Class<T> clazz) {
        this.modelName = modelName;
        this.platform =  (T)LLMConfig.PLATFORM_CONFIGS.get(platformKey);

    }

    public AbstractPlatformService setProxy(Proxy proxy) {
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

    public Response<AiMessage> chat(List<ChatMessage> chatMessageList) {
        if (!isEnabled()) {
            log.error("llm service is disabled");
            throw new RuntimeException("llm service is disabled");
        }
        return getChatLLM().generate(chatMessageList);
    }



}
