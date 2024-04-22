package scl.langchain4j.rag;


import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.FlushResponse;
import io.milvus.grpc.MutationResult;
import io.milvus.grpc.QueryResults;
import io.milvus.grpc.SearchResults;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.*;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.QueryParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.param.partition.CreatePartitionParam;
import io.milvus.param.partition.LoadPartitionsParam;
import io.milvus.param.partition.ReleasePartitionsParam;
import io.milvus.response.QueryResultsWrapper;
import io.milvus.response.SearchResultsWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scl.langchain4j.config.MilvusConfig;
import scl.langchain4j.constants.MilvusConstants;
import scl.utils.MilvusMetadataFilterUtils;
import scl.utils.MilvusUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;

/**
 * @author sichaolong
 * @createdate 2024/4/17 16:07
 */


@Service
public class MilvusService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MilvusService.class);

    @Autowired
    MilvusConfig milvusConfig;

    @Autowired
    MilvusServiceClient milvusClient;


    public MilvusServiceClient getMilvusServerClient() {
        return milvusClient;
    }

    /**
     * 创建集合
     *
     * @param collectionName
     * @param databaseName
     * @param fieldTypeList
     * @return
     */
    public boolean creatCollection(String databaseName, String collectionName, String collectionDesc, List<FieldType> fieldTypeList) {

        if (StringUtils.isEmpty(databaseName)) {
            LOGGER.error("databaseName is null");
            throw new RuntimeException("databaseName is null");
        }
        if (StringUtils.isEmpty(collectionName)) {
            LOGGER.error("collectionName is null");
            throw new RuntimeException("collectionName is null");
        }
        if (CollectionUtils.isEmpty(fieldTypeList)) {
            LOGGER.error("fieldTypeList is null");
            throw new RuntimeException("fieldTypeList is null");
        }

        CreateCollectionParam createCollectionReq = CreateCollectionParam.newBuilder()
            .withDatabaseName(databaseName)
            .withCollectionName(collectionName)
            .withDescription(collectionDesc)
            .withFieldTypes(fieldTypeList)
            .withShardsNum(MilvusConstants.SHARDS_NUM)
            .build();
        R<RpcStatus> response = milvusClient.createCollection(createCollectionReq);
        LOGGER.info(databaseName + "是否成功创建集合——>>" + response.getStatus());
        return MilvusConstants.TURE.equals(response.getStatus()) ? true : false;
    }


    /**
     * 判断集合是否存在
     *
     * @param databaseName
     * @param collectionName
     * @return
     */
    public boolean isExitCollection(String databaseName, String collectionName) {
        if (StringUtils.isEmpty(collectionName)) {
            LOGGER.error("collectionName is null");
            throw new RuntimeException("collectionName is null");
        }
        R<Boolean> response = milvusClient.hasCollection(
            HasCollectionParam.newBuilder()
                .withDatabaseName(databaseName)
                .withCollectionName(collectionName)
                .build());

        return response.getData();
    }

    /**
     * 为集合创建分区
     *
     * @param collectionName
     * @param partitionName
     */
    @Deprecated
    public boolean createPartition(String collectionName, String partitionName) {
        if (StringUtils.isEmpty(collectionName)) {
            LOGGER.error("collectionName is null");
            throw new RuntimeException("collectionName is null");
        }
        if (StringUtils.isEmpty(partitionName)) {
            LOGGER.error("partitionName is null");
            throw new RuntimeException("partitionName is null");
        }
        R<RpcStatus> response = milvusClient.createPartition(CreatePartitionParam.newBuilder()
            .withCollectionName(collectionName) //集合名称
            .withPartitionName(partitionName) //分区名称
            .build());
        LOGGER.info(collectionName + "是否成功创建分区——>>" + response.getStatus());
        return MilvusConstants.TURE.equals(response.getStatus()) ? true : false;
    }


    /**
     * 创建索引
     */
    public boolean createIndex(String collectionName, String fieldName, IndexType indexType, MetricType metricType, String extraParam) {
        if (StringUtils.isEmpty(fieldName)) {
            LOGGER.error("fieldName is null");
            throw new RuntimeException("fieldName is null");
        }

        CreateIndexParam.Builder builder = CreateIndexParam.newBuilder()
            .withCollectionName(collectionName)
            .withFieldName(fieldName)
            .withIndexType(indexType)
            .withMetricType(metricType)
            .withSyncMode(Boolean.FALSE);

        // 可以指定nlist参数，建议值为 4 × sqrt(n)，其中 n 指 segment 最多包含的 entity 条数。
        if (StringUtils.isNotEmpty(extraParam)) {
            builder.withExtraParam(extraParam);
        }

        R<RpcStatus> response = milvusClient.createIndex(builder.build());
        MilvusUtils.checkResponseNotFailed(response);
        LOGGER.info("{} create index success,IndexType:{},MetricType:{}",collectionName,indexType.getName(),metricType.name());
        return response.getStatus() == MilvusConstants.TURE;
    }


    /**
     * 插入数据到集合
     *
     * @param collectionName
     * @param fields
     * @return
     */

    public boolean insert(String databaseName, String collectionName, String partitionName, List<InsertParam.Field> fields) {
        if (StringUtils.isEmpty(databaseName)) {
            LOGGER.error("databaseName is null");
            throw new RuntimeException("databaseName is null");
        }
        if (StringUtils.isEmpty(collectionName)) {
            LOGGER.error("collectionName is null");
            throw new RuntimeException("collectionName is null");
        }
        if (CollectionUtils.isEmpty(fields)) {
            LOGGER.error("fields is null");
            throw new RuntimeException("fields is null");
        }

        //插入
        InsertParam.Builder builder = InsertParam.newBuilder()
            .withCollectionName(collectionName)
            .withDatabaseName(databaseName)
            .withFields(fields);

        // 分片不为空
        if (StringUtils.isNotEmpty(partitionName)) {
            builder.withPartitionName(partitionName);
        }
        R<MutationResult> response = milvusClient.insert(builder.build());
        return response.getStatus().equals(MilvusConstants.TURE) ? true : false;
    }


    /**
     * 加载集合
     */
    public boolean loadCollection(String databaseName, String collectionName) {
        if (StringUtils.isEmpty(collectionName)) {
            LOGGER.error("collectionName is null");
            throw new RuntimeException("collectionName is null");
        }
        R<RpcStatus> response = milvusClient.loadCollection(LoadCollectionParam.newBuilder()
            //集合名称
            .withCollectionName(collectionName)
            .withDatabaseName(databaseName)
            .build());
        return response.getStatus().equals(MilvusConstants.TURE) ? true : false;
    }


    /**
     * 刷新集合
     *
     * @param collectionName
     * @return
     */

    public boolean flash(List<String> collectionName) {
        R<FlushResponse> responseR = milvusClient.flush(FlushParam.newBuilder().withCollectionNames(collectionName).build());
        return responseR.getStatus().equals(MilvusConstants.TURE) ? true : false;
    }

    /**
     * 加载分区
     */

    @Deprecated
    public void loadPartitions(String collectionName, String partitionsName) {
        if (StringUtils.isEmpty(partitionsName)) {
            LOGGER.error("partitionsName is null");
            throw new RuntimeException("partitionsName is null");
        }
        R<RpcStatus> response = milvusClient.loadPartitions(
            LoadPartitionsParam
                .newBuilder()
                //集合名称
                .withCollectionName(collectionName)
                //需要加载的分区名称
                .withPartitionNames(Arrays.asList(partitionsName))
                .build()
        );
        MilvusUtils.checkResponseNotFailed(response);
    }

    /**
     * 从内存中释放集合
     */
    public boolean releaseCollection(String collectionName) {
        R<RpcStatus> response = milvusClient.releaseCollection(ReleaseCollectionParam.newBuilder()
            .withCollectionName(collectionName)
            .build());
        LOGGER.info("releaseCollection------------->{}", response);
        return response.getStatus().equals(MilvusConstants.TURE) ? true : false;
    }

    /**
     * 释放分区
     */
    @Deprecated
    public void releasePartition(String collectionName, String partitionsName) {
        R<RpcStatus> response = milvusClient.releasePartitions(ReleasePartitionsParam.newBuilder()
            .withCollectionName(collectionName)
            .addPartitionName(partitionsName)
            .build());
        MilvusUtils.checkResponseNotFailed(response);
    }


    /**
     * 向量查询
     *
     * @param collectionName
     * @param partitionNameList
     * @param fieldName
     * @param queryVector       支持多个向量
     * @param topK
     * @param outFieldList
     * @return
     */

    public SearchResultsWrapper search(String collectionName, List<String> partitionNameList, String fieldName, List<List<Float>> queryVector, Integer topK, List<String> outFieldList, String metaDataExpr) {

        if (CollectionUtils.isEmpty(partitionNameList)) {
            partitionNameList = new ArrayList<>();
        }

        SearchParam.Builder builder = SearchParam.newBuilder().withCollectionName(collectionName)
            .withVectorFieldName(fieldName)
            .withVectors(queryVector)
            .withTopK(topK)
            .withOutFields(outFieldList)
            .withMetricType(MetricType.COSINE)
            .withConsistencyLevel(ConsistencyLevelEnum.STRONG);

        if (StringUtils.isNotEmpty(metaDataExpr)) {
            builder.withExpr(metaDataExpr);
        }
        if (CollectionUtils.isNotEmpty(partitionNameList)) {
            builder.withPartitionNames(partitionNameList);
        }

        R<SearchResults> response = milvusClient.search(builder.build());
        MilvusUtils.checkResponseNotFailed(response);
        return new SearchResultsWrapper(response.getData().getResults());

    }

    /**
     * 根据条件查询
     *
     * @param ids
     * @return
     */

    public QueryResultsWrapper searchByCondition(String collectionName,
                                                 List<String> ids,
                                                 Metadata metadata) {
        QueryParam request = MilvusUtils.buildQueryRequest(collectionName, ids, metadata, ConsistencyLevelEnum.STRONG);
        R<QueryResults> response = milvusClient.query(request);
        MilvusUtils.checkResponseNotFailed(response);
        return new QueryResultsWrapper(response.getData());
    }


}
