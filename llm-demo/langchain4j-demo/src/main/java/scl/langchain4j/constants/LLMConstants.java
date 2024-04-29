package scl.langchain4j.constants;

import dev.langchain4j.model.input.PromptTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sichaolong
 * @createdate 2024/4/19 14:06
 */
public class LLMConstants {


    public static final PromptTemplate PROMPT_USER_RAG_TEMPLATE = PromptTemplate.from("""
        参考以下已知信息，理解试题考察的知识点、考点等：
        {{information}}
        回答问题，尽量准确解答，注意,回答的内容不能让用户感知到已知信息的存在，下面是问题：
        {{question}}
        输出最终的答案与解析，输出格式按照"【答案】：xxx；【解析】：xxx"
        """);

    public static final PromptTemplate PROMPT_USER_TEMPLATE = PromptTemplate.from("""
        下面请回答问题，确保答案准确，下面是问题：
        {{question}}
        输出最终的答案与解析，输出格式按照"【答案】：xxx；【解析】：xxx"
         """);


    public static final PromptTemplate PROMPT_USER_RAG_TEMPLATE_2 = PromptTemplate.from("""
        根据以下已知信息: 
        {{information}}
        尽可能准确地回答用户的问题,以下是用户的问题:
        {{question}}
        注意,回答的内容不能让用户感知到已知信息的存在
        """);

    public static final PromptTemplate PROMPT_USER_RAG_TEMPLATE_3 = PromptTemplate.from("""
        根据以下已知信息: 
        {{information}}
        下面请解答试题:{{question}}，确保答案正确，输出最终的答案与解析，
        输出格式按照
        "【答案】：xxx（只写英文，首字母小写）；
        【解析】：xxx（首先在每个小题最开始写出考查什么词性，再翻译中文单词或短语的英文释义，英文首字母小写，列举出中文单词或短语的全部英文释义。
        格式为“中文单词或短语表达为xxx，每小题结论都写成故填“x”）；"。语言需要简洁，突出重点，英文翻译要符合习惯和语法。
                
        注意,回答的内容不能让用户感知到已知信息的存在
        """);
    public static final PromptTemplate PROMPT_USER_TEMPLATE_2 = PromptTemplate.from("""
        尽可能准确地回答用户的问题,以下是用户的问题:
                   
        {{question}}
         """);


    public static final PromptTemplate PROMPT_SYSTEM_TEMPLATE = PromptTemplate.from("""
        假设你是一位试题命题专家，现在有数学、英语、语文、物理、生物、地理等学科的不同的5位高级教师协商按步骤来给在校学生提问的试题写答案和解析
        (1) 首先所有教师认真审查试题题干，然后所有教师都写下他们思考分析这个试题的第一个步骤
        (2) 然后与大家分享讨论并根据其他老师思路以及试题考点等信息调整自己思路
        (3) 协商一致后然后所有教师都写下他们思考的下一个步骤并分享
        以此类推，直到所有教师写完他们思考的所有步骤。过程中只要你发现有教师的步骤出错了，就让这位教师重新思考分析，参考其他老师的步骤写出新的步骤，重试3次仍错误就让这个老师离开。
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


        public static final String AZURE_BASE_4 = "base4";
        public static final String AZURE_BASE = "base";
        public static final String AZURE_BASE_4_32K = "base4_32k";
    }


    /**
     * 模型参数
     */

    public class ModelParamKey {
        public static String QIANFAN_ENDPOINT_COMPLETIONS = "completions";
    }

}
