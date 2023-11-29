import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.OpenAIServiceVersion;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.IterableStream;
import com.theokanning.openai.service.SSE;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sichaolong
 * @createdate 2023/11/27 17:58
 */
public class AzureOpenAiService {

    private final OpenAIClient client;

    public AzureOpenAiService(String token, String baseUrl, OpenAIServiceVersion serviceVersion) {
        client = new OpenAIClientBuilder()
            .credential(new AzureKeyCredential(token))
            .endpoint(baseUrl)
            .serviceVersion(serviceVersion)
            .buildClient();
    }


    /**
     * 流式响应
     * @param completionsOptions
     * @return
     */

    public Flowable<SSE> createStreamChatCompletion(ChatCompletionsOptions completionsOptions) {
        IterableStream<ChatCompletions> chatCompletionsStream = client.getChatCompletionsStream(completionsOptions.getModel(), completionsOptions);

        Flowable<SSE> sseFlowable = Flowable.create(emitter -> {
            List<ChatCompletions> resultCompletions = chatCompletionsStream
                .stream()
                // Remove .skip(1) when using Non-Azure OpenAI API
                // Note: the first chat completions can be ignored when using Azure OpenAI service which is a known service bug.
                // TODO: remove .skip(1) when service fix the issue.
                .skip(1)
                .collect(Collectors.toList());

            for (ChatCompletions completion : resultCompletions) {
                SSE sse = new SSE(completion.getChoices().get(0).getDelta().getContent());
                emitter.onNext(sse);
            }
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);
        return sseFlowable;
    }
}


