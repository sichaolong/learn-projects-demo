package scl.langchain4j.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import scl.langchain4j.constants.MilvusConstants;


/**
 * @author sichaolong
 * @createdate 2024/4/17 15:51
 */
@Configuration
@Data
public class MilvusConfig {

    /**
     * milvus配置信息
     */

    @Value("${milvus.host}")
    private String host;
    @Value("${milvus.port}")
    private Integer port;

    /**
     * 是否开启RAG检索增强
     */
    @Value("${milvus.rag.retrieveEmbeddingsOnSearch:true}")
    private boolean retrieveEmbeddingsOnSearch;

    /**
     * 数据切片大小
     */
    @Value("${milvus.rag.maxSegmentSizeInTokens:1000}")
    private Integer maxSegmentSizeInTokens;

    /**
     * 发生数据切片时候切片重叠大小
     */
    @Value("${milvus.rag.maxOverlapSizeInTokens:0}")
    private Integer maxOverlapSizeInTokens;

    /**
     * 召回最大结果数量
     */
    @Value("${milvus.rag.recallMaxResults:3}")
    private Integer recallMaxResults;

    /**
     * 从本地知识库召回的最小相似性得分
     */
    @Value("${milvus.rag.recallMinScore:0.6d}")
    private Double recallMinScore;


    /**
     * miilvus-client
     *
     * @return
     */
    @Bean
    public MilvusServiceClient milvusServiceClient() {
        ConnectParam connectParam = ConnectParam.newBuilder()
            .withHost(host)
            .withPort(port)
            .withDatabaseName(MilvusConstants.DATABASE_NAME)
            .build();
        return new MilvusServiceClient(connectParam);
    }
}