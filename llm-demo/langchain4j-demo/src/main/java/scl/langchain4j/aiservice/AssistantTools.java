package scl.langchain4j.aiservice;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * @author sichaolong
 * @createdate 2024/4/16 11:54
 */
@Component
public class AssistantTools {

    /**
     * This tool is available to {@link Assistant}
     */
    @Tool
    String currentTime() {
        return LocalTime.now().toString();
    }
}