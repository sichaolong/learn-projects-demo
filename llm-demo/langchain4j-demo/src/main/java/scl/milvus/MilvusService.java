package scl.milvus;

import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.GetIndexBuildProgressResponse;
import io.milvus.grpc.MutationResult;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.*;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.param.index.GetIndexBuildProgressParam;
import io.milvus.param.partition.CreatePartitionParam;
import io.milvus.param.partition.LoadPartitionsParam;
import io.milvus.param.partition.ReleasePartitionsParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author sichaolong
 * @createdate 2024/4/17 16:07
 */


@Service
public class MilvusService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MilvusService.class);

    @Autowired
    MilvusConfig milvusConfig;

    /**
     * 创建集合
     *
     * @param collectionName
     * @param databaseName
     * @param fieldTypeList
     * @return
     */
    public boolean creatCollection(String databaseName,String collectionName,String collectionDesc,List<FieldType> fieldTypeList) {

        if(StringUtils.isEmpty(databaseName)){
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

        MilvusServiceClient milvusClient = milvusConfig.milvusServiceClient();
        CreateCollectionParam createCollectionReq = CreateCollectionParam.newBuilder()
            .withDatabaseName(databaseName)
            .withCollectionName(collectionName)
            .withDescription(collectionDesc)
            .withFieldTypes(fieldTypeList)
            //.withShardsNum(MilvusConstants.SHARDS_NUM)
            .build();
        R<RpcStatus> response = milvusClient.createCollection(createCollectionReq);
        LOGGER.info(databaseName + "是否成功创建集合——>>" + response.getStatus());
        return MilvusQuestionsConstants.TURE.equals(response.getStatus()) ? true : false;
    }


    /**
     * 判断集合是否存在
     *
     * @param databaseName
     * @param collectionName
     * @return
     */
    public boolean isExitCollection(String databaseName,String collectionName) {
        if (StringUtils.isEmpty(collectionName)) {
            LOGGER.error("collectionName is null");
            throw new RuntimeException("collectionName is null");
        }
        MilvusServiceClient milvusClient = milvusConfig.milvusServiceClient();
        R<Boolean> response = milvusClient.hasCollection(
            HasCollectionParam.newBuilder()
                .withDatabaseName(databaseName)
                .withCollectionName(collectionName)
                .build());
        return MilvusQuestionsConstants.TURE.equals(response.getStatus()) ? true : false;
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
        MilvusServiceClient milvusClient = milvusConfig.milvusServiceClient();
        R<RpcStatus> response = milvusClient.createPartition(CreatePartitionParam.newBuilder()
            .withCollectionName(collectionName) //集合名称
            .withPartitionName(partitionName) //分区名称
            .build());
        LOGGER.info(collectionName + "是否成功创建分区——>>" + response.getStatus());
        return MilvusQuestionsConstants.TURE.equals(response.getStatus()) ? true : false;
    }


    /**
     * 创建索引
     */
    public R<RpcStatus> createIndex(String databaseName,String collectionName, String fieldName, IndexType indexType, MetricType metricType) {
        if (StringUtils.isEmpty(collectionName)) {
            LOGGER.error("collectionName is null");
            throw new RuntimeException("collectionName is null");
        }
        if (StringUtils.isEmpty(fieldName)) {
            LOGGER.error("fieldName is null");
            throw new RuntimeException("fieldName is null");
        }

        MilvusServiceClient milvusClient = milvusConfig.milvusServiceClient();
        R<RpcStatus> response = milvusClient.createIndex(CreateIndexParam.newBuilder()
            .withDatabaseName(databaseName)
            .withCollectionName(collectionName)
            .withFieldName(fieldName)
            .withIndexType(indexType)
            .withMetricType(metricType)
            //nlist 建议值为 4 × sqrt(n)，其中 n 指 segment 最多包含的 entity 条数。
            .withExtraParam("{\"nlist\":16384}")
            .withSyncMode(Boolean.FALSE)
            .build());
        LOGGER.info("createIndex-------------------->{}", response.toString());
        return response;
    }



    /**
     * 插入数据到集合
     * @param collectionName
     * @param fields
     * @return
     */

    public boolean insert(String databaseName,String collectionName,String partitionName,List<InsertParam.Field> fields) {
        if(StringUtils.isEmpty(databaseName)){
            LOGGER.error("databaseName is null");
            throw new RuntimeException("databaseName is null");
        }
        if (StringUtils.isEmpty(collectionName)) {
            LOGGER.error("collectionName is null");
            throw new RuntimeException("collectionName is null");
        }

        MilvusServiceClient milvusClient = milvusConfig.milvusServiceClient();
        //插入
        InsertParam insertParam = InsertParam.newBuilder()
            .withCollectionName(collectionName)
            .withDatabaseName(databaseName)
            .withFields(fields)
            .build();

        // 分片不为空
        if(StringUtils.isNotEmpty(partitionName)){
            insertParam = InsertParam.newBuilder()
                .withCollectionName(collectionName)
                .withDatabaseName(databaseName)
                .withFields(fields)
                .build();
        }
        R<MutationResult> insert = milvusClient.insert(insertParam);
        LOGGER.info("插入:{}", insert);
        LOGGER.info(insert.getStatus().equals(MilvusQuestionsConstants.TURE) ? "InsertRequest successfully! Total number of " +
            "inserts:{" + insert.getData().getInsertCnt() + "} entities" : "InsertRequest failed!");
        return insert.getStatus().equals(MilvusQuestionsConstants.TURE) ? true : false;
    }


    /**
     * 加载集合
     * */
    public boolean loadCollection(String databaseName, String collectionName) {
        if (StringUtils.isEmpty(collectionName)){
            LOGGER.error("collectionName is null");
            throw new RuntimeException("collectionName is null");
        }
        MilvusServiceClient milvusClient = milvusConfig.milvusServiceClient();
        R<RpcStatus> response = milvusClient.loadCollection(LoadCollectionParam.newBuilder()
            //集合名称
            .withCollectionName(collectionName)
            .withDatabaseName(databaseName)
            .build());
        LOGGER.info("loadCollection------------->{}", response);
        return response.getStatus().equals(MilvusQuestionsConstants.TURE) ? true : false;
    }

    /**
     * 加载分区
     * */

    @Deprecated
    public void loadPartitions(String collectionName, String partitionsName) {
        if (StringUtils.isEmpty(partitionsName)){
            LOGGER.error("partitionsName is null");
            throw new RuntimeException("partitionsName is null");
        }
        MilvusServiceClient milvusClient = milvusConfig.milvusServiceClient();
        R<RpcStatus> response = milvusClient.loadPartitions(
            LoadPartitionsParam
                .newBuilder()
                //集合名称
                .withCollectionName(collectionName)
                //需要加载的分区名称
                .withPartitionNames(Arrays.asList(partitionsName))
                .build()
        );
        LOGGER.info("loadCollection------------->{}", response);
    }

    /**
     * 从内存中释放集合
     * */
    public boolean releaseCollection(String collectionName) {
        MilvusServiceClient milvusClient = milvusConfig.milvusServiceClient();
        R<RpcStatus> response = milvusClient.releaseCollection(ReleaseCollectionParam.newBuilder()
            .withCollectionName(collectionName)
            .build());
        LOGGER.info("releaseCollection------------->{}", response);
        return response.getStatus().equals(MilvusQuestionsConstants.TURE) ? true : false;
    }

    /**
     * 释放分区
     * */
    @Deprecated
    public void releasePartition(String collectionName, String partitionsName) {
        MilvusServiceClient milvusClient = milvusConfig.milvusServiceClient();
        R<RpcStatus> response = milvusClient.releasePartitions(ReleasePartitionsParam.newBuilder()
            .withCollectionName(collectionName)
            .addPartitionName(partitionsName)
            .build());
        LOGGER.info("releasePartition------------->{}", response);
    }


}
