package com.moyz.adi.common.cosntant;

import dev.langchain4j.model.input.PromptTemplate;

import java.util.List;

public class AdiConstant {

    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 验证码id过期时间：1小时
     */
    public static final int AUTH_CAPTCHA_ID_EXPIRE = 1;

    /**
     * 验证码过期时间，5分钟
     */
    public static final int AUTH_CAPTCHA_EXPIRE = 5;

    /**
     * 注册激活码有效时长，8小时
     */
    public static final int AUTH_ACTIVE_CODE_EXPIRE = 8;

    /**
     * token存活时间（8小时）
     */
    public static final int USER_TOKEN_EXPIRE = 8;

    public static final String DEFAULT_PASSWORD = "123456";

    public static final int LOGIN_MAX_FAIL_TIMES = 3;

    public static final String[] WEB_RESOURCES = {
            "/swagger-ui/index.html",
            "/swagger-ui",
            "/swagger-resources",
            "/v3/api-docs",
            "/favicon.ico",
            ".css",
            ".js",
            "/doc.html"
    };

    public static final int SECRET_KEY_TYPE_SYSTEM = 1;
    public static final int SECRET_KEY_TYPE_CUSTOM = 2;

    public static final String OPENAI_MESSAGE_DONE_FLAG = "[DONE]";

    public static final String DEFAULT_MODEL = "gpt-3.5-turbo";

    public static final String CREATE_IMAGE_RESP_FORMATS_B64JSON = "b64_json";
    public static final String OPENAI_CREATE_IMAGE_RESP_FORMATS_URL = "url";

    public static final List<String> OPENAI_CREATE_IMAGE_SIZES = List.of("256x256", "512x512", "1024x1024");

    public static final PromptTemplate PROMPT_TEMPLATE = PromptTemplate.from("""
            根据以下已知信息:
            {{information}}
            尽可能准确地回答用户的问题,以下是用户的问题:
            {{question}}
            注意,回答的内容不能让用户感知到已知信息的存在
            """);

    public static class GenerateImage {
        public static final int INTERACTING_METHOD_GENERATE_IMAGE = 1;
        public static final int INTERACTING_METHOD_EDIT_IMAGE = 2;
        public static final int INTERACTING_METHOD_VARIATION = 3;

        public static final int STATUS_DOING = 1;
        public static final int STATUS_FAIL = 2;
        public static final int STATUS_SUCCESS = 3;
    }

    public static class EmbeddingMetadataKey {
        public static final String KB_UUID = "kb_uuid";
        public static final String KB_ITEM_UUID = "kb_item_uuid";
        public static final String ENGINE_NAME = "engine_name";
        public static final String SEARCH_UUID = "search_uuid";
    }

    public static class SysConfigKey {
        public static final String OPENAI_SETTING = "openai_setting";
        public static final String DASHSCOPE_SETTING = "dashscope_setting";
        public static final String QIANFAN_SETTING = "qianfan_setting";
        public static final String OLLAMA_SETTING = "ollama_setting";
        public static final String GOOGLE_SETTING = "google_setting";
        public static final String BING_SETTING = "bing_setting";
        public static final String BAIDU_SETTING = "baidu_setting";
        public static final String REQUEST_TEXT_RATE_LIMIT = "request_text_rate_limit";
        public static final String REQUEST_IMAGE_RATE_LIMIT = "request_image_rate_limit";
        public static final String CONVERSATION_MAX_NUM = "conversation_max_num";
        public static final String QUOTA_BY_TOKEN_DAILY = "quota_by_token_daily";
        public static final String QUOTA_BY_TOKEN_MONTHLY = "quota_by_token_monthly";
        public static final String QUOTA_BY_REQUEST_DAILY = "quota_by_request_daily";
        public static final String QUOTA_BY_REQUEST_MONTHLY = "quota_by_request_monthly";
        public static final String QUOTA_BY_IMAGE_DAILY = "quota_by_image_daily";
        public static final String QUOTA_BY_IMAGE_MONTHLY = "quota_by_image_monthly";
        public static final String QUOTA_BY_QA_ASK_DAILY = "quota_by_qa_ask_daily";
    }

    public static final String[] POI_DOC_TYPES = {"doc", "docx", "ppt", "pptx", "xls", "xlsx"};

    public static class SearchEngineName {
        public static final String GOOGLE = "google";
        public static final String BING = "bing";
        public static final String BAIDU = "baidu";
    }

    public static class SSEEventName {
        public static final String START = "[START]";
        public static final String DONE = "[DONE]";
        public static final String ERROR = "[ERROR]";

        public static final String AI_SEARCH_SOURCE_LINKS = "[SOURCE_LINKS]";
    }

    public static final int RAG_TYPE_KB = 1;
    public static final int RAG_TYPE_SEARCH = 2;
}
