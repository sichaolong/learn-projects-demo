package scl.langchain4j.examples.qianfan;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.qianfan.QianfanChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scl.langchain4j.assistant.Assistant;
import scl.langchain4j.assistant.AssistantTools;

/**
 * @author sichaolong
 * @createdate 2024/4/16 13:39
 */
public class QianfanDemo {


    private static final Logger LOGGER = LoggerFactory.getLogger(QianfanDemo.class);

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.configure().load();

        String apiKey = dotenv.get("QIANFAN_API_KEY");
        String secretKey = dotenv.get("QIANFAN_SECRET_KEY");
        String modelName = "ERNIE-3.5-8K";
        String endpoint = "completions";

        System.out.println(String.format("The current QIANFAN_API_KEY is: %s.", dotenv.get("QIANFAN_API_KEY")));
        System.out.println(String.format("The current QIANFAN_SECRET_KEY is: %s.", dotenv.get("QIANFAN_API_KEY")));



        /*
        用途是纪录与LLM对话中的历史信息，LLM正是通过对话上下文，来生成更正确的推理内容。
        根据千帆大模型技术规定，对话信息必须是单数个，代码中设定为99，这个长度足够实验使用。
        如果设定为双数个，比如100，当对话到达100个信息后，千帆会报错。
        在多用户应用场景中，每个用户与LLM的对话应该相互独立，因此LangChain4J支持通过不同的MemoryId来区分内部的ChatMemory。
         */
        // 历史对话
        ChatMemoryProvider chatMemoryProvider = (memoryId) -> {
            return MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(99)
                .chatMemoryStore(new InMemoryChatMemoryStore())
                .build();
        };



        // qianfan model
        QianfanChatModel qianfanChatModel = QianfanChatModel.builder()
            .apiKey(apiKey)
            .secretKey(secretKey)
            .modelName(modelName)
            .endpoint(endpoint)
            .build();

        // tools
        AssistantTools assistantTool = new AssistantTools();

        // assistant
        Assistant assistant = AiServices.builder(Assistant.class)
            .chatLanguageModel(qianfanChatModel)
            .chatMemoryProvider(chatMemoryProvider)
            // .tools(assistantTool)
            .build();

        LOGGER.info("LangChain4J AiServices are initialized, we are using ERNIE-3.5-8K model.");


        String question = "你好";
        String response = assistant.chatSimple("user1", question);
        System.out.println(response);

    }
}
