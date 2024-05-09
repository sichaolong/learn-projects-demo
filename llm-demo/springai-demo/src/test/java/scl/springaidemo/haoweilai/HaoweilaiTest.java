package scl.springaidemo.haoweilai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import scl.springaidemo.SpringaiDemoApplication;
import scl.springaidemo.demos.haoweilai.HaoweilaiAiChatClient;
import scl.springaidemo.demos.haoweilai.HaoweilaiChatOptions;
import scl.springaidemo.demos.haoweilai.HaoweilaiMessage;

import java.util.Arrays;
import java.util.List;

/**
 * @author sichaolong
 * @createdate 2024/5/9 08:47
 */

@SpringBootTest(classes = SpringaiDemoApplication.class)
public class HaoweilaiTest {

    @Autowired
    HaoweilaiAiChatClient chatClient;

    @Test
    public void testHaoweilaiChat() {


        HaoweilaiMessage userMessage = HaoweilaiMessage.builder()
            .role(MessageType.USER.getValue())
            .content("你好")
            .build();
        List<Message> messages = Arrays.asList(userMessage);
        ChatOptions chatOptions = HaoweilaiChatOptions.builder()
            .stream(false)
            .n(1)
            .subject("1")
            .build();
        ChatResponse response = chatClient.call(new Prompt(messages,chatOptions));

        System.out.println(response);

        /**
         * {
         *     "code": 20000,
         *     "msg": "success",
         *     "requestId": "17152362156831238135957338763264",
         *     "action": "result",
         *     "data": {
         *         "result": "您好!今天我能为您提供什么帮助?",
         *         "is_end": 1,
         *         "mod": "multi-70b",
         *         "prompt_tokens": 43,
         *         "total_tokens": 60,
         *         "completion_tokens": 17,
         *         "logprobs": {
         *             "top_logprobs": null
         *         },
         *         "total_score": 0
         *     }
         * }
         */
    }

    @Test
    public void testHaoweilaiStreamChat() {


        HaoweilaiMessage userMessage = HaoweilaiMessage.builder()
            .role(MessageType.USER.getValue())
            .content("你好")
            .build();
        List<Message> messages = Arrays.asList(userMessage);
        ChatOptions chatOptions = HaoweilaiChatOptions.builder()
            .stream(false)
            .n(1)
            .subject("1")
            .build();
        Flux<ChatResponse> responseFlux = chatClient.stream(new Prompt(messages, chatOptions));
        List<ChatResponse> chatResponseList = responseFlux.collectList().cache().block();
        System.out.println(chatResponseList);
    }
}
