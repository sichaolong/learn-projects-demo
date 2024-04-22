package scl.langchain4j.constants;

import dev.langchain4j.model.input.PromptTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sichaolong
 * @createdate 2024/4/19 14:06
 */
public class LLMConstants {

    public static final PromptTemplate PROMPT_TEMPLATE = PromptTemplate.from("""
            根据以下已知信息:
            {{information}}
            尽可能准确地回答用户的问题,以下是用户的问题:
            {{question}}
            注意,回答的内容不能让用户感知到已知信息的存在
            """);

    public static final Map<String, String> CONFIGS = new HashMap<>();

}
