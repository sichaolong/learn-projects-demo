package scl.langchain4j.constants;

import dev.langchain4j.model.input.PromptTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sichaolong
 * @createdate 2024/4/19 14:06
 */
public class LLMConstants {

    public static final PromptTemplate PROMPT_TEMPLATE_RAG = PromptTemplate.from("""
            根据以下已知信息: 
            {{information}}
            尽可能准确地回答用户的问题,以下是用户的问题:
            {{question}}
            注意,回答的内容不能让用户感知到已知信息的存在
            """);


    public static final PromptTemplate PROMPT_TEMPLATE = PromptTemplate.from("""
            尽可能准确地回答用户的问题,以下是用户的问题:
            {{question}}
            注意,回答的内容不能让用户感知到已知信息的存在
            """);



    /**
     * LLM平台
     */

    public class PlatformKey {
        public static final String QIANFAN = "qianfan";
        public static final String OPENAI = "openai";
        public static final String AZURE = "azure";
    }


    /**
     * LLM模型
     */
    public class ModelKey {
        public static final String QIANFAN_ERNIE_4_0_8K = "ERNIE-4.0-8K";
        public static final String QIANFAN_ERNIE_3_5_8K = "ERNIE-3.5-8K";
    }


    /**
     * 模型参数
     */

    public class ModelParamKey{
        public static String QIANFAN_ENDPOINT_COMPLETIONS = "completions";
    }

}
