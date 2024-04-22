package scl.langchain4j.llm;
import lombok.Data;

/**
 * @author sichaolong
 * @createdate 2024/4/19 14:09
 */
@Data
public class ModelInfo extends AbstractModelInfo {

    private AbstractLLMService llmService;
}