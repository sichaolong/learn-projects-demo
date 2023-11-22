import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.DataType;
import io.milvus.grpc.ListDatabasesResponse;
import io.milvus.grpc.MutationResult;
import io.milvus.grpc.SearchResults;
import io.milvus.param.*;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.CreateDatabaseParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.response.SearchResultsWrapper;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author sichaolong
 * @createdate 2023/11/13 16:35
 */
public class Demo {

    /**
     * 创建连接
     */
    private static final MilvusServiceClient milvusClient = new MilvusServiceClient(
        ConnectParam.newBuilder()
            .withHost("localhost")
            .withPort(19530)
            .build()
    );

    public static void main(String[] args) {
        // testCreateDataBase();
        // testListDataBases();
        // testCreateCollection();
        // testInsertData();
        // testCreateIndex();
        //  testLoadDocument();
        testSearch();
    }

    public static void testCreateDataBase() {
        CreateDatabaseParam param = CreateDatabaseParam.newBuilder()
            .withDatabaseName("mydb")
            .build();
        R<RpcStatus> response = milvusClient.createDatabase(param);
        if (response.getStatus() != R.Status.Success.getCode()) {
            System.out.println(response.getMessage());
        }

    }

    public static void testListDataBases() {
        R<ListDatabasesResponse> response = milvusClient.listDatabases();
        System.out.println(response.getData());
        if (response.getStatus() != R.Status.Success.getCode()) {
            System.out.println(response.getMessage());
        }
    }

    public static void testCreateCollection() {

        List<FieldType> fieldsSchema = new ArrayList<>();
        FieldType field_1 = FieldType.newBuilder()
            .withPrimaryKey(true)
            .withAutoID(false)
            .withDataType(DataType.Int64)
            .withName("uid")
            .withDescription("unique id")
            .build();

        fieldsSchema.add(field_1);

        /**
         *      None(0),
         *     Bool(1),
         *     Int8(2),
         *     Int16(3),
         *     Int32(4),
         *     Int64(5),
         *     Float(10),
         *     Double(11),
         *     String(20),
         *     VarChar(21),
         *     Array(22),
         *     JSON(23),
         *     BinaryVector(100),
         *     FloatVector(101),
         *     Float16Vector(102),
         *     UNRECOGNIZED(-1);
         */
        FieldType field_2 = FieldType.newBuilder()
            .withDataType(DataType.FloatVector)
            .withName("embedding")
            .withDescription("embeddings")
            // 向量的维度
            .withDimension(3)
            .build();
        fieldsSchema.add(field_2);

        // create collection
        CreateCollectionParam param = CreateCollectionParam.newBuilder()
            .withCollectionName("mycollection")
            .withDescription("a collection for search")
            .withFieldTypes(fieldsSchema)
            .build();

        R<RpcStatus> response = milvusClient.createCollection(param);
        if (response.getStatus() != R.Status.Success.getCode()) {
            System.out.println(response.getMessage());
        }

    }

    public static void testInsertData(){

        int rowCount = 100;
        List<Long> ids = new ArrayList<>();
        for (long i = 0L; i < rowCount; ++i) {
            ids.add(i);
        }
        List<List<Float>> embeddings = generateFloatVectors(rowCount);

        List<InsertParam.Field> fieldsInsert = new ArrayList<>();
        fieldsInsert.add(new InsertParam.Field("uid", ids));
        fieldsInsert.add(new InsertParam.Field("embedding", embeddings));

        InsertParam param = InsertParam.newBuilder()
            .withDatabaseName("default")
            .withCollectionName("mycollection")
            .withFields(fieldsInsert)
            .build();
        R<MutationResult> response = milvusClient.insert(param);
        System.out.println(response);
        if (response.getStatus() != R.Status.Success.getCode()) {
            System.out.println(response.getMessage());
        }

    }

    private static List<List<Float>> generateFloatVectors(int rowCount) {
        List<List<Float>> list = new LinkedList<>();

        for (int i = 0; i < rowCount; i++) {
            list.add(Arrays.asList(RandomUtils.nextFloat(),RandomUtils.nextFloat(),RandomUtils.nextFloat()));
        }
        return list;

    }


    public static void testCreateIndex(){

        CreateIndexParam param = CreateIndexParam.newBuilder()
            .withCollectionName("mycollection")
            .withFieldName("embedding")
            .withIndexType(IndexType.IVF_FLAT)
            .withMetricType(MetricType.L2)
            .withExtraParam("{\"nlist\":64}")
            .build();
        R<RpcStatus> response = milvusClient.createIndex(param);
        if (response.getStatus() != R.Status.Success.getCode()) {
            System.out.println(response.getMessage());
        }

    }

    public static void testLoadDocument(){

        LoadCollectionParam param = LoadCollectionParam.newBuilder()
            .withCollectionName("mycollection")
            .withReplicaNumber(0)
            .withSyncLoad(Boolean.TRUE)
            .withSyncLoadWaitingInterval(500L)
            .withSyncLoadWaitingTimeout(30L)
            .build();
        R<RpcStatus> response = milvusClient.loadCollection(param);
        if (response.getStatus() != R.Status.Success.getCode()) {
            System.out.println(response.getMessage());
        }

    }

    public static void testSearch(){
        final Integer SEARCH_K = 2;                                     // TopK
        final String SEARCH_PARAM = "{\"nprobe\":10, \"offset\":0}";    // Params

        List<String> search_output_fields = Arrays.asList("embedding");
        List<List<Float>> search_vectors = Arrays.asList(Arrays.asList(0.1f, 0.2f));

        SearchParam searchParam = SearchParam.newBuilder()
            .withCollectionName("mycollection")
            .withConsistencyLevel(ConsistencyLevelEnum.STRONG)
            .withMetricType(MetricType.L2)
            .withOutFields(search_output_fields)
            .withTopK(SEARCH_K)
            .withVectors(search_vectors)
            .withVectorFieldName("embedding")
            .withParams(SEARCH_PARAM)
            .build();
        R<SearchResults> respSearch = milvusClient.search(searchParam);
//        SearchResultsWrapper wrapperSearch = new SearchResultsWrapper(respSearch.getData().getResults());
//        System.out.println(wrapperSearch.getIDScore(0));
//        System.out.println(wrapperSearch.getFieldData("embedding", 0));



    }
}
