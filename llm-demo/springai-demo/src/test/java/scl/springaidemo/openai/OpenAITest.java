package scl.springaidemo.openai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import scl.springaidemo.SpringaiDemoApplication;

/**
 * @author sichaolong
 * @createdate 2024/5/8 10:47
 */

@SpringBootTest(classes = SpringaiDemoApplication.class)
public class OpenAITest {


    @Autowired
    OpenAiChatClient chatClient;

    @Test
    public void testOpenAIChat() {
        ChatResponse response = chatClient.call(
            new Prompt(
                "你好",
                OpenAiChatOptions.builder()
                    .withModel("gpt-4")
                    .withTemperature(0.4f)
                    .build()
            ));

        System.out.println(response);
    }
}
