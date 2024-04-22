package scl.langchain4j.assistant;

import dev.langchain4j.service.*;

/**
 * @author sichaolong
 * @createdate 2024/4/16 13:37
 */
public interface Assistant {

    @SystemMessage("You are a polite assistant")
    String chatSimple(@MemoryId String memoryId, @UserMessage String userMessage);

    @SystemMessage("{{sm}}")
    TokenStream chat(@MemoryId String memoryId, @V("sm") String systemMessage, @UserMessage String prompt);

    TokenStream chat(@MemoryId String memoryId, @UserMessage String prompt);
}
