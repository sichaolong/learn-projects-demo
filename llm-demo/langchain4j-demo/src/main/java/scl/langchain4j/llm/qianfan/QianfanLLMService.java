package scl.langchain4j.llm.qianfan;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import scl.langchain4j.llm.AbstractLLMService;

/**
 * @author sichaolong
 * @createdate 2024/4/19 16:19
 */
public class QianfanLLMService extends AbstractLLMService<QianfanPlatform> {


    public QianfanLLMService(String modelName, String settingName, Class<QianfanPlatform> clazz) {
        super(modelName, settingName, clazz);
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    protected ChatLanguageModel buildChatLLM() {
        return null;
    }

    @Override
    protected StreamingChatLanguageModel buildStreamingChatLLM() {
        return null;
    }

    @Override
    protected String parseError(Object error) {
        return null;
    }
}
