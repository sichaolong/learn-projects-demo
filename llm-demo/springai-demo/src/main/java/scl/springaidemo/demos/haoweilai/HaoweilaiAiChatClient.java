package scl.springaidemo.demos.haoweilai;

import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestMessage;
import org.slf4j.Logger;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.StreamingChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.function.AbstractFunctionCallSupport;
import org.springframework.ai.model.function.FunctionCallbackContext;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author sichaolong
 * @createdate 2024/5/8 16:30
 */
public class HaoweilaiAiChatClient extends AbstractFunctionCallSupport<ChatRequestMessage, ChatCompletionsOptions, ChatCompletions> implements ChatClient, StreamingChatClient {


    private final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(HaoweilaiAiChatClient.class);


    private static final String DEFAULT_DEPLOYMENT_NAME = "ERNIE-3.5";
    private static final Float DEFAULT_TEMPERATURE = 0.7F;
    private AzureOpenAiChatOptions defaultOptions;

    public HaoweilaiAiChatClient(FunctionCallbackContext functionCallbackContext) {
        super(functionCallbackContext);
    }

    @Override
    public ChatResponse call(Prompt prompt) {
        return null;
    }

    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
        return null;
    }

    @Override
    protected ChatCompletionsOptions doCreateToolResponseRequest(ChatCompletionsOptions previousRequest, ChatRequestMessage responseMessage, List<ChatRequestMessage> conversationHistory) {
        return null;
    }

    @Override
    protected List<ChatRequestMessage> doGetUserMessages(ChatCompletionsOptions request) {
        return null;
    }

    @Override
    protected ChatRequestMessage doGetToolResponseMessage(ChatCompletions response) {
        return null;
    }

    @Override
    protected ChatCompletions doChatCompletion(ChatCompletionsOptions request) {
        return null;
    }

    @Override
    protected boolean isToolFunctionCall(ChatCompletions response) {
        return false;
    }
}
