package scl.langchain4j.assistant;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * @author sichaolong
 * @createdate 2024/4/19 15:41
 */
public interface AssistantWithoutMemory {

    @SystemMessage("{{sm}}")
    TokenStream chat(@V("sm") String systemMessage, @UserMessage String prompt);

    TokenStream chat(@UserMessage String prompt);
}
