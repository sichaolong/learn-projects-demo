package scl.kanning;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import io.reactivex.Flowable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * @author sichaolong
 * @createdate 2023/10/10 09:49
 * github：https://github.com/TheoKanning/openai-java
 */
public class KanningOpenAIClientDemo {

    public static String OPENAI_TOKEN = "xxx";

    public static OpenAiService service;


    static {
        // 加上魔法代理 or  其他方式，魔法代理否则报错，参考：https://www.6hu.cc/archives/162598.html
        // java.net.ConnectException:Failed to connect to api.openai.com/2a03:2880:f10c:283:face:b00c:0:25de:443]

        // 1、需求额外设置一个能访问chatGPT的魔法访问署理
        // ObjectMapper mapper = OpenAiService.defaultObjectMapper();
        // Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8889));
        // OkHttpClient client = OpenAiService.defaultClient(OPENAI_TOKEN, Duration.ofSeconds(10000))
        //     .newBuilder()
        //     .proxy(proxy)
        //     .build();
        // Retrofit retrofit = OpenAiService.defaultRetrofit(client, mapper);
        // OpenAiApi api = retrofit.create(OpenAiApi.class);

        // 其他方式

        //将设置的署理传给OpenAiService即可
        service = new OpenAiService(OPENAI_TOKEN);
    }
    public static void main(String[] args) {

        // showModels();

        // send1Msg();
        sendStreamMsg();
    }


    /**
     * 显示模型
     * 根据不同的功能和语言选择合适的模型，可以在官网的模型概述中查看
     * https://platform.openai.com/docs/models/overview
     */
    public static void showModels() {
        //列出所有模型实例
        System.out.println(service.listModels());
        //检索模型,得到模型实例，提供有关模型的基本信息，例如所有者和权限，应用场景等。
        System.out.println(service.getModel("text-davinci-003"));
    }

    /**
     * 加上魔法代理
     * @throws InterruptedException
     */

    public static void send1Msg() throws InterruptedException {

        //需求额外设置一个能访问chatGPT的魔法访问署理
        ObjectMapper mapper = OpenAiService.defaultObjectMapper();
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8889));
        OkHttpClient client = OpenAiService.defaultClient(OPENAI_TOKEN, Duration.ofSeconds(10000))
            .newBuilder()
            .proxy(proxy)
            .build();
        Retrofit retrofit = OpenAiService.defaultRetrofit(client, mapper);
        OpenAiApi api = retrofit.create(OpenAiApi.class);
        //将设置的署理传给OpenAiService即可
        OpenAiService service = new OpenAiService(api);
        System.out.println("开始发问题～");
        CompletionRequest completionRequest = CompletionRequest.builder()
            .model("text-davinci-003")
            .prompt("你是一个作业帮手，情帮忙规划一份活动策划书，规划一份活动策划书")
            .temperature(0.5)
            .maxTokens(2048)
            .topP(1D)
            .build();
        service.createCompletion(completionRequest).getChoices().forEach(System.out::println);
        Thread.sleep(6000);
    }

    public static void sendStreamMsg() {
        System.out.println("Streaming chat completion...");
        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine();
        // Create a list of ChatMessage objects
        List<ChatMessage> message = new ArrayList<ChatMessage>();
        message.add(new ChatMessage(ChatMessageRole.USER.value(), userInput));
        // Create a ChatCompletionRequest object
        ChatCompletionRequest chatCompletionRequest;
        boolean running = true;
        // Run the loop until the user enters "exit"
        while (running) {
            chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo")
                .messages(message)
                .n(1)
                .maxTokens(500)
                .logitBias(Collections.emptyMap())
                .build();
            // Create a Flowable object to stream the chat completion
            Flowable<ChatCompletionChunk> flowableResult = service.streamChatCompletion(chatCompletionRequest);
            // Create a StringBuilder object to store the result
            StringBuilder buffer = new StringBuilder();
            // Subscribe to the Flowable object and print the result
            flowableResult.subscribe(chunk -> {
                chunk.getChoices().forEach(choice -> {
                    String result = choice.getMessage().getContent();
                    if (result != null) {
                        buffer.append(result);
                        System.out.print(choice.getMessage().getContent());
                    }
                });
            }, Throwable::printStackTrace, () -> System.out.println());
            // Get the user input
            userInput = scanner.nextLine();
            // Add the user input to the list of ChatMessage objects
            message.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), buffer.toString()));
            message.add(new ChatMessage(ChatMessageRole.USER.value(), userInput));
            // Exit the loop if the user enters "exit"
            if (userInput.equals("exit")) {
                running = false;
            }
        }
        scanner.close();
        service.shutdownExecutor();
    }

}
