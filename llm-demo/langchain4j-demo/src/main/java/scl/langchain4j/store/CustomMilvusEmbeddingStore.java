package scl.langchain4j.store;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.internal.Utils;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.DataType;
import io.milvus.grpc.SearchResults;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.collection.FieldType;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.response.SearchResultsWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import scl.langchain4j.config.MilvusConfig;
import scl.langchain4j.constants.MilvusConstants;
import scl.langchain4j.rag.MilvusService;
import scl.utils.MilvusMetadataFilterUtils;
import scl.utils.MilvusUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;


/**
 * @author sichaolong
 * @createdate 2024/4/19 11:35
 * ps: 自定义本地向量知识库组件，因为官方包里的MilvusEmbeddingStore创建索引那块有问题，
 */

@Slf4j
public class CustomMilvusEmbeddingStore implements EmbeddingStore<TextSegment>, InitializingBean {


    /**
     * 是否开启RAG检索增强生成,默认开启
     */
    private boolean retrieveEmbeddingsOnSearch = true;

    private String collectionName;

    private String databaseName;

    private MetricType metricType;

    private ConsistencyLevelEnum consistencyLevel;

    @Autowired
    MilvusService milvusService;

    @Autowired
    MilvusConfig milvusConfig;

    public CustomMilvusEmbeddingStore() {
    }

    public CustomMilvusEmbeddingStore(String databaseName, String collectionName, MetricType metricType, ConsistencyLevelEnum consistencyLevel) {
        this.collectionName = collectionName;
        this.databaseName = databaseName;
        this.metricType = metricType;
        this.consistencyLevel = consistencyLevel;
    }


    /**
     * 是否开启检索增强生成
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        this.retrieveEmbeddingsOnSearch = milvusConfig.isRetrieveEmbeddingsOnSearch();
        log.info("是否开启增强检索生成:{}", retrieveEmbeddingsOnSearch);
        if(retrieveEmbeddingsOnSearch){
            initMilvusCollection(databaseName,collectionName);
        } else {
            closeMilvusCollection(databaseName,collectionName);
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
            boolean success = milvusService.creatCollection(databaseName, collectionName, MilvusConstants.COLLECTION_NAME_QUESTIONS_ENGLISH_GZ_DESC, fieldTypeList);
            if (success) {
                milvusService.createIndex(collectionName,
                    MilvusConstants.Field.EIGENVALUES,
                    MilvusConstants.IVF_FLAT_INDEX_TYPE,
                    MilvusConstants.METRIC_TYPE_ENGLISH_GZ,
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


    @Override
    public String add(Embedding embedding) {
        String id = Utils.randomUUID();
        add(id, embedding);
        return id;
    }
    @Override
    public void add(String id, Embedding embedding) {
        addInternal(id, embedding, null);
    }

    @Override
    public String add(Embedding embedding, TextSegment textSegment) {
        String id = Utils.randomUUID();
        addInternal(id, embedding, textSegment);
        return id;
    }

    @Override
    public List<String> addAll(List<Embedding> embeddings) {
        List<String> ids = MilvusUtils.generateRandomIds(embeddings.size());
        addAllInternal(ids, embeddings, null);
        return ids;
    }

    @Override
    public List<String> addAll(List<Embedding> embeddings, List<TextSegment> embedded) {
        List<String> ids = MilvusUtils.generateRandomIds(embeddings.size());
        addAllInternal(ids, embeddings, embedded);
        return ids;
    }

    @Override
    public EmbeddingSearchResult<TextSegment> search(EmbeddingSearchRequest embeddingSearchRequest) {

        SearchParam searchParam = buildSearchRequest(
            collectionName,
            embeddingSearchRequest.queryEmbedding().vectorAsList(),
            embeddingSearchRequest.filter(),
            embeddingSearchRequest.maxResults(),
            metricType,
            consistencyLevel
        );
        R<SearchResults> resultsR = milvusService.getMilvusServerClient().search(searchParam);
        SearchResultsWrapper resultsWrapper = new SearchResultsWrapper(resultsR.getData().getResults());
        List<EmbeddingMatch<TextSegment>> matches = MilvusUtils.toEmbeddingMatches(
            milvusService.getMilvusServerClient(),
            resultsWrapper,
            collectionName,
            consistencyLevel,
            retrieveEmbeddingsOnSearch
        );
        List<EmbeddingMatch<TextSegment>> result = matches.stream()
            .filter(match -> match.score() >= embeddingSearchRequest.minScore())
            .collect(toList());

        return new EmbeddingSearchResult<>(result);
    }

    private  SearchParam buildSearchRequest(String collectionName,
                                                 List<Float> vector,
                                                 Filter filter,
                                                 int maxResults,
                                                 MetricType metricType,
                                                 ConsistencyLevelEnum consistencyLevel) {
        SearchParam.Builder builder = SearchParam.newBuilder()
            .withCollectionName(collectionName)
            .withVectors(singletonList(vector))
            .withVectorFieldName(MilvusConstants.Field.EIGENVALUES)
            .withTopK(maxResults)
            .withMetricType(metricType)
            .withConsistencyLevel(consistencyLevel)
            .withOutFields(asList(MilvusConstants.Field.ID,MilvusConstants.Field.METADATA,MilvusConstants.Field.EIGENVALUES,MilvusConstants.Field.QUESTION_CONTENT));

        if (filter != null) {
            builder.withExpr(MilvusMetadataFilterUtils.map(filter));
        }

        return builder.build();
    }


    private void addInternal(String id, Embedding embedding, TextSegment textSegment) {
        addAllInternal(
            singletonList(id),
            singletonList(embedding),
            textSegment == null ? null : singletonList(textSegment)
        );
    }

    private void addAllInternal(List<String> ids,List<Embedding> embeddings, List<TextSegment> textSegments) {
        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field(MilvusConstants.Field.ID, ids));
        fields.add(new InsertParam.Field(MilvusConstants.Field.QUESTION_CONTENT, MilvusUtils.toScalars(textSegments, ids.size())));
        fields.add(new InsertParam.Field(MilvusConstants.Field.EIGENVALUES, MilvusUtils.toVectors(embeddings)));
        fields.add(new InsertParam.Field(MilvusConstants.Field.METADATA, MilvusUtils.toMetadataJsons(textSegments, embeddings.size())));

        milvusService.insert(databaseName, collectionName, null, fields);
        milvusService.flash(Arrays.asList(collectionName));
    }

}
