package scl.springaidemo;

import org.junit.jupiter.api.Test;
import org.springframework.ai.azure.openai.AzureOpenAiChatClient;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author sichaolong
 * @createdate 2024/5/8 10:47
 */

@SpringBootTest(classes = SpringaiDemoApplication.class)
public class AzureTest {


    @Autowired
    AzureOpenAiChatClient chatClient;

    @Test
    public void testAzureChat() {
        ChatResponse response = chatClient.call(
            new Prompt(
                "Generate the names of 5 famous pirates.",
                AzureOpenAiChatOptions.builder()
                    .withDeploymentName("base4")
                    .withTemperature(0.4f)
                    .build()
            ));

        System.out.println(response);
    }
}
