package scl.springaidemo.demos.haoweilai;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.StreamingChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.ModelOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import scl.springaidemo.demos.haoweilai.config.HaoweilaiConfig;
import scl.springaidemo.demos.haoweilai.signature.enums.RequestMethod;
import scl.springaidemo.demos.haoweilai.signature.sign.GetWSSign;
import scl.springaidemo.demos.haoweilai.signature.sign.SendSignHttp;
import scl.springaidemo.demos.haoweilai.signature.util.DateUtil;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;

import static scl.springaidemo.demos.haoweilai.signature.util.HttpUtil.APPLICATION_JSON;


/**
 * @author sichaolong
 * @createdate 2024/5/8 16:30
 */

@Service
public class HaoweilaiAiChatClient implements ChatClient, StreamingChatClient {

    private final Logger LOGGER = LoggerFactory.getLogger(HaoweilaiAiChatClient.class);

    private HaoweilaiChatOptions defaultHaoweilaiChatOptions;

    @Autowired
    private HaoweilaiConfig properties;

    public HaoweilaiAiChatClient() {
    }


    @Override
    public ChatResponse call(Prompt prompt) {
        initChatOptions(prompt);

        String accessKey = properties.getAccessKey();
        String secretKey = properties.getSecretKey();
        String url = String.format("%s%s", properties.getHttpBaseUrl(), properties.getHttpPath());
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> body = Map.of("messages", prompt.getInstructions(),
            "subject", defaultHaoweilaiChatOptions.getSubject(),
            "n", defaultHaoweilaiChatOptions.getN(),
            "stream", defaultHaoweilaiChatOptions.isStream());
        try {
            String response = signAndCallApi(accessKey, secretKey, url, RequestMethod.POST, body, params);
            LOGGER.info("messages: {}", prompt.getInstructions());
            LOGGER.info("response: {}", response);
            return transformMsgToChatResponse(response);
        } catch (Exception e) {
            LOGGER.error("call api error:{}", e);
            throw new RuntimeException(String.format("调用好未来API发生异常！"));
        }
    }


    /**
     * 初始化haoweilaiChatOptions
     *
     * @param prompt
     */

    private void initChatOptions(Prompt prompt) {
        if (Objects.isNull(properties)
            || StringUtils.isEmpty(properties.getAccessKey())
            || StringUtils.isEmpty(properties.getSecretKey())) {
            throw new RuntimeException("haoweilai connection properties accessKey or secretKey is null");
        }

        // 默认options
        defaultHaoweilaiChatOptions = HaoweilaiChatOptions.builder()
            .subject(properties.getSubject())
            .stream(properties.isStream())
            .n(properties.getN())
            .build();

        // 自定义options
        ModelOptions chatOptions = prompt.getOptions();
        if (Objects.nonNull(chatOptions) && chatOptions instanceof HaoweilaiChatOptions) {
            HaoweilaiChatOptions customChatOptions = (HaoweilaiChatOptions) chatOptions;
            if (StringUtils.isNotEmpty(customChatOptions.getSubject())) {
                defaultHaoweilaiChatOptions.setSubject(customChatOptions.getSubject());
            }
            if (Objects.nonNull(customChatOptions.getN())) {
                defaultHaoweilaiChatOptions.setN(customChatOptions.getN());
            }
            if (Objects.nonNull(customChatOptions.isStream())) {
                defaultHaoweilaiChatOptions.setStream(customChatOptions.isStream());
            }
        }


    }

    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
        initChatOptions(prompt);

        String accessKey = properties.getAccessKey();
        String secretKey = properties.getSecretKey();
        String url = String.format("%s%s", properties.getWssBaseUrl(), properties.getWssPath());
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> body = Map.of("messages", prompt.getInstructions(),
            "subject", defaultHaoweilaiChatOptions.getSubject(),
            "n", defaultHaoweilaiChatOptions.getN(),
            "stream", defaultHaoweilaiChatOptions.isStream());
        try {
            Flux<ChatResponse> response = signAndCallStreamApi(accessKey, secretKey, url, params, body);
            return response;
        } catch (Exception e) {
            LOGGER.error("call strem api error:{}", e);
            throw new RuntimeException(String.format("调用好未来Stream API发生异常！"));
        }
    }

    /**
     * 签名授权并调用API
     *
     * @param accessKey
     * @param secretKey
     * @param url
     * @param requestMethod
     * @param body
     * @param params
     * @return
     * @throws Exception
     */
    private String signAndCallApi(String accessKey, String secretKey, String url, RequestMethod requestMethod, Map<String, Object> body, Map<String, Object> params) throws Exception {

        String contentType = APPLICATION_JSON;
        //当前时间（东8区）
        Date timestamp = DateUtil.getCurrentDate();
        /**
         * 获取签名鉴权，并发送请求
         */
        HttpResponse httpResponse = SendSignHttp.sendRequest(
            accessKey,
            secretKey,
            timestamp,
            url,
            params,
            body,
            requestMethod,
            contentType);
        //响应结果httpResponse
        return EntityUtils.toString(httpResponse.getEntity(), Charset.defaultCharset());
    }


    /**
     * 签名授权并调用Stream API
     *
     * @param accessKey
     * @param secretKey
     * @param url
     * @param body
     * @param params
     * @return
     * @throws Exception
     */
    private Flux<ChatResponse> signAndCallStreamApi(String accessKey, String secretKey, String url, Map<String, Object> params, Map<String, Object> body) throws Exception {
        //获取当前时间（东8区）
        Date timestamp = DateUtil.getCurrentDate();
        url = GetWSSign.getSign(
            url,
            accessKey,
            secretKey,
            timestamp,
            params,
            body);

        final Flux<ChatResponse>[] chatResponseFlux = new Flux[]{null};
        WebSocketClient client = new WebSocketClient(new URI(url), new Draft_6455(), null, 10000) {

            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                LOGGER.info("Connection is opened");
            }

            @Override
            public void onMessage(String msg) {
                LOGGER.info("received:" + msg);
                chatResponseFlux[0] = Flux.create(sink -> {
                    sink.next(transformMsgToChatResponse(msg));
                });
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                LOGGER.info("code：" + code);
                LOGGER.info("reason：" + reason);
                LOGGER.info("remote：" + remote);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                chatResponseFlux[0] = Flux.create(sink -> {
                    sink.next(transformErrorToChatResponse(e.getMessage()));
                    sink.complete();
                });
            }
        };

        client.connect();
        LOGGER.info("draft:{}", client.getDraft());

        while (!client.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
            LOGGER.info("还没有打开");
            Thread.sleep(1000);
        }


//        LOGGER.info("");
//        for (int i = 0; i < 10; i++) {
//            client.send("hello world:" + System.currentTimeMillis());
//            Thread.sleep(1000);
//        }
        return chatResponseFlux[0];
    }


    /**
     * 将response error 转为 ChatResponse
     *
     * @param error
     * @return
     */
    private ChatResponse transformErrorToChatResponse(String error) {
        return new ChatResponse(Arrays.asList(new Generation(error)));
    }

    /**
     * 将response msg 转为 ChatResponse
     *
     * @param msg
     */
    private ChatResponse transformMsgToChatResponse(String msg) {
        String result = null;
        HaoweilaiResponse haoweilaiResponse = JSONObject.parseObject(msg, HaoweilaiResponse.class);
        if (haoweilaiResponse.getCode() == 20000 && StringUtils.isNotEmpty(haoweilaiResponse.getData().getResult())) {
            result = haoweilaiResponse.getData().getResult();
        }
        return new ChatResponse(Arrays.asList(new Generation(result)));
    }
}
