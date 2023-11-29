/**
 * @author sichaolong
 * @createdate 2023/11/29 09:06
 */
/*
 * Copyright (C) 2023 the xkw.com authors
 * http://www.xkw.com
 */



import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.*;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.IterableStream;
import com.theokanning.openai.service.SSE;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author sichaolong
 * @createdate 2023/11/28 14:08
 */
public class AzureOpenAIClient {

    public static void getChatCompletion(){
        String azureOpenaiKey = "azure-key";
        String endpoint = "https://xxx.openai.azure.com/";
        String deploymentOrModelId = "base4";

        OpenAIClient client = new OpenAIClientBuilder()
            .endpoint(endpoint)
            .credential(new AzureKeyCredential(azureOpenaiKey))
            .buildClient();

        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatMessage(ChatRole.SYSTEM,"你是一个机器人"));
        chatMessages.add(new ChatMessage(ChatRole.USER,"你好"));

        ChatCompletions chatCompletions = client.getChatCompletions(deploymentOrModelId, new ChatCompletionsOptions(chatMessages));

        for (ChatChoice choice : chatCompletions.getChoices()) {
            ChatMessage message = choice.getMessage();
            System.out.printf("Index: %d, Chat Role: %s.%n", choice.getIndex(), message.getRole());
            System.out.println("Message:");
            System.out.println(message.getContent());
        }

        System.out.println();
        CompletionsUsage usage = chatCompletions.getUsage();
        System.out.printf("Usage: number of prompt token is %d, "
                + "number of completion token is %d, and number of total tokens in request and response is %d.%n",
            usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
    }

    /**
     * 流式响应
     * @return
     */

    public static Flowable<SSE> getStreamChatCompletion() {

        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatMessage(ChatRole.SYSTEM,"你是一个机器人"));
        chatMessages.add(new ChatMessage(ChatRole.USER,"你好"));

        String azureOpenaiKey = "8d728a3da84146a7a55396a7e8abb3ea";
        String endpoint = "https://xkwopenai.openai.azure.com/";
        String deploymentOrModelId = "base4";

        OpenAIClient client = new OpenAIClientBuilder()
            .endpoint(endpoint)
            .credential(new AzureKeyCredential(azureOpenaiKey))
            .buildClient();
        IterableStream<ChatCompletions> chatCompletionsStream = client.getChatCompletionsStream(deploymentOrModelId, new ChatCompletionsOptions(chatMessages));
        if(chatCompletionsStream.stream().iterator().hasNext()){
            System.out.println("has content");
        }
        List<ChatCompletions> resultCompletions = chatCompletionsStream
            .stream()
            // Remove .skip(1) when using Non-Azure OpenAI API
            // Note: the first chat completions can be ignored when using Azure OpenAI service which is a known service bug.
            // TODO: remove .skip(1) when service fix the issue.
            .skip(1)
            .collect(Collectors.toList());
        for (ChatCompletions resultCompletion : resultCompletions) {
            System.out.println(resultCompletion.getChoices().get(0).getDelta().getContent());
        }

//        Flowable<SSE> sseFlowable = Flowable.create(emitter -> {
//            List<ChatCompletions> resultCompletions = chatCompletionsStream
//                .stream()
//                // Remove .skip(1) when using Non-Azure OpenAI API
//                // Note: the first chat completions can be ignored when using Azure OpenAI service which is a known service bug.
//                // TODO: remove .skip(1) when service fix the issue.
//                //.skip(1)
//                .collect(Collectors.toList());
//            System.out.println(resultCompletions);
//
//            for (ChatCompletions completion : resultCompletions) {
//                String content = completion.getChoices().get(0).getDelta().getContent();
//                SSE sse = new SSE(content);
//                emitter.onNext(sse);
//            }
//            emitter.onComplete();
//        }, BackpressureStrategy.BUFFER);
//        return sseFlowable;

        return null;
    }

    public static void main(String[] args) {
//        getChatCompletion();
        getStreamChatCompletion();

    }

}
