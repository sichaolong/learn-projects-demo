package scl.langchain4j.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.Strings;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import scl.langchain4j.constants.LLMConstants;
import scl.langchain4j.llm.Azure.AzurePlatformInfo;
import scl.langchain4j.llm.Azure.AzurePlatformService;
import scl.langchain4j.llm.LLMContext;
import scl.langchain4j.llm.qianfan.QianfanPlatformService;
import scl.langchain4j.llm.qianfan.QianfanPlatformInfo;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sichaolong
 * @createdate 2024/4/22 14:40
 */

@Configuration
@Data
@Slf4j
public class LLMConfig implements InitializingBean {

    @Value("${llms.proxy.enable:false}")
    protected boolean proxyEnable;

    @Value("${llms.proxy.host:0}")
    protected String proxyHost;

    @Value("${llms.proxy.http-port:0}")
    protected int proxyHttpPort;


    /**
     * 千帆
     */
    @Value("${llms.qianfan.apiKey:VCoRxgkoKMofRTw4LixDPfcX}")
    private String qianfanApiKey;

    @Value("${llms.qianfan.apiKey:Pxlaz0AzBF111Ki35fCuPndBx52Qax7F}")
    private String qianfanSecretKey;

    @Value("${llms.qianfan.models:ERNIE-4.0-8K,ERNIE-3.5-8K}")
    private String qianfanModels;


    /**
     * 微软Azure
     */

    @Value("${llms.azure.apiKey:8d728a3da84146a7a55396a7e8abb3ea}")
    private String azureApiKey;

    @Value("${llms.azure.apiKey:}")
    private String azureSecretKey;

    @Value("${llms.azure.models:base4}")
    private String azureModels;




    public static Map<String, Object> PLATFORM_CONFIGS = new HashMap<>();


    /**
     * 读取配置，加载LLM上下文
     *
     * @throws Exception
     */

    @Override
    public void afterPropertiesSet() {

        Proxy proxy = null;
        if (proxyEnable) {
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyHttpPort));
        }

        // 千帆
        initQianfanLLMPlatform(proxy);
        // 微软Azure
        initAzureLLMPlatform(proxy);
    }


    /**
     * 百度千帆
     *
     * @param proxy
     */
    private void initQianfanLLMPlatform(Proxy proxy) {

        QianfanPlatformInfo platform = new QianfanPlatformInfo();
        platform.setQianfanApiKey(qianfanApiKey);
        platform.setQianfanSecretKey(qianfanSecretKey);
        String[] models = StringUtils.split(qianfanModels, ",");
        platform.setModels(models);
        PLATFORM_CONFIGS.put(LLMConstants.PlatformKey.QIANFAN, platform);

        String[] qianfanModels = LLMContext.getSupportModels(LLMConstants.PlatformKey.QIANFAN);
        if (qianfanModels.length == 0) {
            log.warn("qianfan platform service is disabled");
        }
        log.info("qianfan avilable model:{}", qianfanModels);

        for (String model : qianfanModels) {
            LLMContext.addLLMService(model, new QianfanPlatformService(model).setProxy(proxy));
        }
    }


    /**
     * 微软Azure
     *
     * @param proxy
     */
    private void initAzureLLMPlatform(Proxy proxy) {

        AzurePlatformInfo platform = new AzurePlatformInfo();
        platform.setAzureApiKey(azureApiKey);
        // TODO 设置参数
        String[] models = StringUtils.split(azureModels, ",");
        platform.setModels(models);
        PLATFORM_CONFIGS.put(LLMConstants.PlatformKey.AZURE, platform);

        String[] azureModels = LLMContext.getSupportModels(LLMConstants.PlatformKey.AZURE);
        if (azureModels.length == 0) {
            log.warn("azure platform service is disabled");
        }
        log.info("azure avilable model:{}", qianfanModels);

        for (String model : azureModels) {
            LLMContext.addLLMService(model, new AzurePlatformService(model).setProxy(proxy));
        }
    }


}
