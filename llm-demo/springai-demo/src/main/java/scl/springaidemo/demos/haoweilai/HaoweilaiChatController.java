package scl.springaidemo.demos.haoweilai;

import org.springframework.ai.azure.openai.AzureOpenAiChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * @author sichaolong
 * @createdate 2024/5/8 11:02
 */

@RestController
public class HaoweilaiChatController {

    private final HaoweilaiAiChatClient chatClient;

    @Autowired
    public HaoweilaiChatController(HaoweilaiAiChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/haoweilai/ai/generate")
    public Map generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("generation", chatClient.call(message));
    }



    @GetMapping("/haoweilai/ai/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatClient.stream(prompt);
    }
}