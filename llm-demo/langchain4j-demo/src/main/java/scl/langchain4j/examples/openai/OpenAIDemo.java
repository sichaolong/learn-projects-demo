package scl.langchain4j.examples.openai;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

/**
 * @author sichaolong
 * @createdate 2024/4/16 11:13
 * 需要代理
 */
public class OpenAIDemo {

    public static void main(String[] args) {

        String apiKey = "sk-xxx";
        OpenAiChatModel model = OpenAiChatModel.withApiKey(apiKey);
        String answer = model.generate("Hello world!");
        System.out.println(answer); // Hello! How can I assist you today?
    }
}
