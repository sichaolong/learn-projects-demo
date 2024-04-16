package scl.langchain4j.aiservice;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
/**
 * @author sichaolong
 * @createdate 2024/4/16 13:37
 */
public interface Assistant {

    @SystemMessage("You are a polite assistant")
    String chat(@MemoryId String memoryId, @UserMessage String userMessage);
}
