package scl.langchain4j.rag;


import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;

import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.DataType;
import io.milvus.param.collection.FieldType;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.InitializingBean;

import org.springframework.stereotype.Service;
import scl.langchain4j.config.MilvusConfig;
import scl.langchain4j.constants.MilvusConstants;
import scl.langchain4j.pojo.KnowledgeBaseItem;
import scl.langchain4j.store.CustomMilvusEmbeddingStore;

import java.util.*;
import java.util.stream.Collectors;

import static dev.langchain4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;


/**
 * @author sichaolong
 * @createdate 2024/4/19 16:52
 * 构建Milvus本地知识向量库
 */
@Service
@Slf4j
public class KnowledgeBaseService implements InitializingBean {

    private static final String KNOWLEDGE_ITEM_FORMAT = "【题干】：%s；【答案】：%s；【解析】：%s";

    public static final EmbeddingModel EMBEDDING_MODEL = new AllMiniLmL6V2EmbeddingModel();

    public Map<String, CustomMilvusEmbeddingStore> EMBEDDING_STORE_MAP = new HashMap<>();

    public Map<CustomMilvusEmbeddingStore, EmbeddingStoreIngestor> EMBEDDING_STORE_INGESTOR_MAP = new HashMap<>();


    @Resource
    MilvusConfig milvusConfig;

    @Resource
    MilvusService milvusService;


    /**
     * 初始化
     * 1、向量存储组件
     * 2、文档切分组件
     * ps：按照collectionName划分，一个collection对应一个embeddingStore
     *
     * @return
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        CustomMilvusEmbeddingStore store1 = new CustomMilvusEmbeddingStore(
            MilvusConstants.DATABASE_NAME,
            MilvusConstants.Collection.COLLECTION_NAME_QUESTIONS_ENGLISH_28_23,
            MilvusConstants.METRIC_TYPE_COSINE,
            ConsistencyLevelEnum.STRONG,
            milvusService,
            milvusConfig);

        CustomMilvusEmbeddingStore store2 = new CustomMilvusEmbeddingStore(
            MilvusConstants.DATABASE_NAME,
            MilvusConstants.Collection.COLLECTION_NAME_QUESTIONS_ENGLISH_3_030602,
            MilvusConstants.METRIC_TYPE_COSINE,
            ConsistencyLevelEnum.STRONG,
            milvusService,
            milvusConfig);

        EMBEDDING_STORE_MAP.put(MilvusConstants.Collection.COLLECTION_NAME_QUESTIONS_ENGLISH_28_23, store1);
        EMBEDDING_STORE_MAP.put(MilvusConstants.Collection.COLLECTION_NAME_QUESTIONS_ENGLISH_3_030602, store2);

        EMBEDDING_STORE_INGESTOR_MAP.put(store1, createEmbeddingStoreIngestor(store1));
        EMBEDDING_STORE_INGESTOR_MAP.put(store2, createEmbeddingStoreIngestor(store2));

        retrieveEnable(MilvusConstants.Collection.COLLECTION_NAME_QUESTIONS_ENGLISH_28_23);
        retrieveEnable(MilvusConstants.Collection.COLLECTION_NAME_QUESTIONS_ENGLISH_3_030602);
    }


    /**
     * 创建文档切分组件
     *
     * @return
     */
    private EmbeddingStoreIngestor createEmbeddingStoreIngestor(CustomMilvusEmbeddingStore store) {
        DocumentSplitter documentSplitter = DocumentSplitters.recursive(milvusConfig.getMaxSegmentSizeInTokens(),
            milvusConfig.getMaxOverlapSizeInTokens(),
            new OpenAiTokenizer(GPT_3_5_TURBO));

        EmbeddingStoreIngestor embeddingStoreIngestor = EmbeddingStoreIngestor.builder()
            .documentSplitter(documentSplitter)
            .embeddingModel(EMBEDDING_MODEL)
            .embeddingStore(store)
            .build();
        return embeddingStoreIngestor;
    }


    /**
     * 数据切分入库
     *
     * @param item
     * @return
     */
    public void embeddingAndStore(String collectionName, KnowledgeBaseItem item) {
        embeddingAndStore(collectionName, Arrays.asList(item));
    }

    public void embeddingAndStore(String collectionName, List<KnowledgeBaseItem> itemList) {
        if (CollectionUtils.isEmpty(itemList)) {
            return;
        }
        List<Document> documentList = new ArrayList<>();
        for (KnowledgeBaseItem item : itemList) {
            // log.info("knowledge item:{}", JSON.toJSONString(item));
            String content = String.format(KNOWLEDGE_ITEM_FORMAT, item.getStem(), item.getAnswer(), item.getExplanation());
            Metadata metadata = new Metadata();
            metadata.add(MilvusConstants.Field.QUESTION_ID, item.getQid());
            metadata.add(MilvusConstants.Field.QUESTION_COURSEID, item.getCourseId());
            metadata.add(MilvusConstants.Field.QUESTION_TYPEID, item.getTypeId());
            Document document = new Document(content, metadata);
            documentList.add(document);
        }
        ingestDocuments(collectionName, documentList);
    }


    private void ingestDocument(String collectionName, Document document) {
        CustomMilvusEmbeddingStore store = getEmbeddingStoreByCollectionName(collectionName);
        getEmbeddingStoreIngestor(store).ingest(document, store);
    }

    private void ingestDocuments(String collectionName, List<Document> documentList) {
        CustomMilvusEmbeddingStore store = getEmbeddingStoreByCollectionName(collectionName);
        getEmbeddingStoreIngestor(store).ingest(documentList, store);
    }

    /**
     * 根据collectionName获取EmbeddingStore
     *
     * @param collectionName
     * @return
     */
    CustomMilvusEmbeddingStore getEmbeddingStoreByCollectionName(String collectionName) {
        Map<String, CustomMilvusEmbeddingStore> embeddingStoreMap = EMBEDDING_STORE_MAP;
        if (MapUtils.isEmpty(embeddingStoreMap)) {
            throw new RuntimeException("no embedding store");
        }
        return embeddingStoreMap.values()
            .stream()
            .collect(Collectors.toList())
            .stream()
            .filter(embeddingStore -> embeddingStore.getCollectionName().equals(collectionName))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("no embedding store"));
    }

    EmbeddingStoreIngestor getEmbeddingStoreIngestor(CustomMilvusEmbeddingStore store) {
        EmbeddingStoreIngestor ingestor = EMBEDDING_STORE_INGESTOR_MAP.getOrDefault(store, null);
        if(ingestor == null){
            throw new RuntimeException("no embedding store ingestor");
        }
        return ingestor;
    }


    /**
     * 是否开启检索增强生成
     * @throws Exception
     */

    public void retrieveEnable(String collectionName) {
        log.info("是否开启增强检索生成:{}", milvusConfig.isRetrieveEmbeddingsOnSearch());
        if(milvusConfig.isRetrieveEmbeddingsOnSearch()){
            initMilvusCollection(MilvusConstants.DATABASE_NAME,collectionName);
        }
    }


    /**
     * milvus本地知识库
     */
    private void initMilvusCollection(String databaseName, String collectionName) {
        boolean exist = milvusService.isExitCollection(databaseName, collectionName);
        log.info("本地向量库是否存在:{}", exist);
        if (!exist) {
            List<FieldType> fieldTypeList = buildFieldTypeList();
            boolean success = milvusService.creatCollection(databaseName, collectionName, MilvusConstants.Collection.COLLECTION_NAME_QUESTIONS_ENGLISH_28_23, fieldTypeList);
            if (success) {
                milvusService.createIndex(collectionName,
                    MilvusConstants.Field.EIGENVALUES,
                    MilvusConstants.IVF_FLAT_INDEX_TYPE,
                    MilvusConstants.METRIC_TYPE_COSINE,
                    MilvusConstants.IVF_INDEX_EXTRA_PARAM_ENGLISH_GZ);
            }
        }
        boolean success = milvusService.loadCollection(databaseName, collectionName);
        log.info("加载 milvus 的 database：{}, collection:{} 结果状态：{}", databaseName, collectionName, success);
    }

    private void closeMilvusCollection(String databaseName,String collectionName) {
        boolean success = milvusService.releaseCollection(collectionName);
        log.info("释放 milvus 的 database：{}, collection:{} 结果状态：{}", databaseName, collectionName, success);
    }


    /**
     * 创建collection的字段
     *
     * @return
     */
    private List<FieldType> buildFieldTypeList() {
        List<FieldType> fieldTypeList = new ArrayList<>();
        FieldType id = FieldType.newBuilder()
            .withName(MilvusConstants.Field.ID)
            .withDescription(MilvusConstants.Field.ID_DESC)
            .withDataType(DataType.VarChar)
            .withMaxLength(MilvusConstants.Field.ID_MAX_LENGTH)
            .withPrimaryKey(true)
            .build();


        FieldType content = FieldType.newBuilder()
            .withName(MilvusConstants.Field.QUESTION_CONTENT)
            .withDescription(MilvusConstants.Field.QUESTION_CONTENT_DESC)
            .withDataType(DataType.VarChar)
            .withMaxLength(MilvusConstants.Field.QUESTION_CONTENT_MAX_LENGTH)
            .build();

        FieldType metadata = FieldType.newBuilder()
            .withName(MilvusConstants.Field.METADATA)
            .withDescription(MilvusConstants.Field.METADATA_DESC)
            .withDataType(DataType.JSON)
            .withMaxLength(MilvusConstants.Field.METADATA_MAX_LENGTH)
            .build();

        FieldType eigenvalues = FieldType.newBuilder()
            .withName(MilvusConstants.Field.EIGENVALUES)
            .withDescription(MilvusConstants.Field.EIGENVALUES_DESC)
            .withDataType(DataType.FloatVector)
            .withDimension(MilvusConstants.FEATURE_DIM)
            .build();

        fieldTypeList.add(id);
        fieldTypeList.add(content);
        fieldTypeList.add(metadata);
        fieldTypeList.add(eigenvalues);
        return fieldTypeList;

    }



}
