package scl.langchain4j.config;

import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.param.ConnectParam;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import scl.langchain4j.constants.MilvusConstants;
import scl.langchain4j.store.CustomMilvusEmbeddingStore;

import static dev.langchain4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

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


    /**
     * 知识库milvus向量存储组件
     *
     * @return
     */
    @Bean
    public CustomMilvusEmbeddingStore milvusEmbeddingStore() {
        CustomMilvusEmbeddingStore embeddingStore = new CustomMilvusEmbeddingStore(
            MilvusConstants.DATABASE_NAME,
            MilvusConstants.Collection.COLLECTION_NAME_QUESTIONS_ENGLISH_28_23,
            MilvusConstants.METRIC_TYPE_COSINE,
            ConsistencyLevelEnum.STRONG);
        return embeddingStore;
    }


    /**
     * 本地知识库Embedding组件
     */
    @Bean
    public EmbeddingModel embeddingModel() {
        // 维度384
        return new AllMiniLmL6V2EmbeddingModel();
    }


    /**
     * 本地知识库文档切分组件
     *
     * @return
     */
    @Bean
    public EmbeddingStoreIngestor embeddingStoreIngestor() {
        DocumentSplitter documentSplitter = DocumentSplitters.recursive(maxSegmentSizeInTokens, maxOverlapSizeInTokens, new OpenAiTokenizer(GPT_3_5_TURBO));
        EmbeddingStoreIngestor embeddingStoreIngestor = EmbeddingStoreIngestor.builder()
            .documentSplitter(documentSplitter)
            .embeddingModel(embeddingModel())
            .embeddingStore(milvusEmbeddingStore())
            .build();
        return embeddingStoreIngestor;
    }
}