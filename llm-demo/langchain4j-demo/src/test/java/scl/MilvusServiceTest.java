package scl;

import com.alibaba.fastjson.JSON;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.DataType;
import io.milvus.grpc.SearchResults;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.collection.FieldType;
import io.milvus.param.dml.InsertParam;
import io.milvus.response.QueryResultsWrapper;
import io.milvus.response.SearchResultsWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import scl.langchain4j.constants.MilvusConstants;
import scl.langchain4j.rag.MilvusService;
import scl.utils.MilvusUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author sichaolong
 * @createdate 2024/4/17 16:53
 */
@SpringBootTest
@Slf4j
public class MilvusServiceTest {


    /**
     * 1. attu作为可视化工具，可以用来做简单的查询和删除以及集合、分区创建的操作，但是对于复杂的操作，建议还是代码实现。
     * 2. 插入数据到到矢量库后，并不会马上可以查询到插入后的数据（针对一次性插入1万条数据以上，只要insertf的status状态反馈为0，则表示插入成功。一般需要等待半个小时以内时间才可以，删除情况类型）
     * 3. 矢量库不会永久性的删除数据，总数entities只会增加不会减少，但是删除的数据只要delete的status状态反馈为0，则表示删除成功。通过条件查询时，删除后的数据一定是查询无结果的。
     * 4. Int64类型只能用Long去对应存放
     */

    @Autowired
    MilvusService milvusService;


    private List<FieldType> buildFieldTypeList() {
        List<FieldType> fieldTypeList = new ArrayList<>();
        FieldType id = FieldType.newBuilder()
            .withName(MilvusConstants.Field.ID)
            .withDescription(MilvusConstants.Field.ID_DESC)
            .withDataType(DataType.VarChar)
            .withMaxLength(MilvusConstants.Field.METADATA_MAX_LENGTH)
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

    /**
     * 1、测试创建集合
     */
    @Test
    public void testCreateCollection() {

        List<FieldType> fieldTypeList = buildFieldTypeList();
        boolean success = milvusService.creatCollection(MilvusConstants.DATABASE_NAME,
            MilvusConstants.COLLECTION_NAME_QUESTIONS_ENGLISH_GZ,
            MilvusConstants.COLLECTION_NAME_QUESTIONS_ENGLISH_GZ_DESC,
            fieldTypeList);
        System.out.println(success);
    }


    /**
     * 2、创建索引（插入数据之后）
     */
    @Test
    public void testCreateIndex() {
        boolean success = milvusService.createIndex(
            MilvusConstants.COLLECTION_NAME_QUESTIONS_ENGLISH_GZ,
            MilvusConstants.Field.EIGENVALUES,
            MilvusConstants.HNSW_INDEX_TYPE,
            MilvusConstants.METRIC_TYPE_ENGLISH_GZ,
            // nlist
            // ef、M
            MilvusConstants.HNSW_INDEX_EXTRA_PARAM_ENGLISH_GZ);
        System.out.println(success);
    }

    /**
     * 3、加载Collection
     */
    @Test
    public void testLoadCollection() {
        boolean success = milvusService.loadCollection(MilvusConstants.DATABASE_NAME, MilvusConstants.COLLECTION_NAME_QUESTIONS_ENGLISH_GZ);
        System.out.println(success);
    }


    /**
     * 4、创建分区
     */
    @Test
    public void testCreatePartition() {
        boolean success = milvusService.createPartition(MilvusConstants.COLLECTION_NAME_QUESTIONS_ENGLISH_GZ, MilvusConstants.PARTITION_PREFIX + 1);
        System.out.println(success);
    }


    /**
     * 5、测试集合是否存在
     */
    @Test
    public void testIsExitCollection() {
        boolean exits = milvusService.isExitCollection(MilvusConstants.DATABASE_NAME, MilvusConstants.COLLECTION_NAME_QUESTIONS_ENGLISH_GZ);
        System.out.println(exits);
    }


    /**
     * 6、测试插入
     */
    @Test
    public void testInsert() {

        String databaseName = MilvusConstants.DATABASE_NAME;
        String collectionName = MilvusConstants.COLLECTION_NAME_QUESTIONS_ENGLISH_GZ;
        // 模拟数据
        List<List<Float>> eigenValues = new ArrayList<>();
        // 128维度
        List<Float> vectorItem = new ArrayList<>();
        for (int i = 1; i <= 128; i++) {
            vectorItem.add(i * 0.1f);
        }
        eigenValues.add(vectorItem);

        // metadata
        Metadata metadata = new Metadata();
        metadata.put(MilvusConstants.Field.QUESTION_ID, "3476004932208640");
        metadata.put(MilvusConstants.Field.QUESTION_CONTENT, "sichaolong");
        TextSegment textSegment = new TextSegment("sichaolong test text", metadata);

        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field(MilvusConstants.Field.EIGENVALUES, eigenValues));
        fields.add(new InsertParam.Field(MilvusConstants.Field.METADATA, MilvusUtils.toMetadataJsons(Arrays.asList(textSegment), eigenValues.size())));

        boolean success = milvusService.insert(databaseName, collectionName, null, fields);
        System.out.println(success);
    }


    /**
     * 7、测试刷新
     * ps：插入数据之后刷新下在查询
     */
    @Test
    public void testFlash() {
        List<String> collectionNameList = Arrays.asList(MilvusConstants.COLLECTION_NAME_QUESTIONS_ENGLISH_GZ);
        boolean success = milvusService.flash(collectionNameList);
        System.out.println(success);
    }


    /**
     * 8、测试查询
     */

    @Test
    public void testSearch() {

        String collectionName = MilvusConstants.COLLECTION_NAME_QUESTIONS_ENGLISH_GZ;
        List<String> outFiledList = Arrays.asList(MilvusConstants.Field.METADATA, MilvusConstants.Field.EIGENVALUES, MilvusConstants.Field.ID);

        List<Float> vector = new ArrayList<>();
        for (int i = 0; i < 128; i++) {
            vector.add(i * 0.1f);
        }

        SearchResultsWrapper wrapper = milvusService.search(collectionName,
            null,
            MilvusConstants.Field.EIGENVALUES,
            Arrays.asList(vector),
            MilvusConstants.TOP_K,
            outFiledList, null);
        log.info("search results wrapper:{}", wrapper);
        log.info("id:{}", wrapper.getFieldWrapper(MilvusConstants.Field.ID).getFieldData());
        log.info("eigen_values:{}", wrapper.getFieldWrapper(MilvusConstants.Field.EIGENVALUES).getFieldData());
        log.info("metadata:{}", JSON.toJSONString(wrapper.getFieldWrapper(MilvusConstants.Field.METADATA).getFieldData()));


    }


    /**
     * 9、测试条件查询
     */

    @Test
    public void testSearchByCondition() {
        // 条件是或者的关系
        List<String> ids = Arrays.asList("449166796404446634", "449166796404446632");

        // 条件是并列的关系
        Metadata condition = new Metadata();
        condition.put("qid", "3476004932208640");
        condition.put("content", "sichaolong");

        QueryResultsWrapper wrapper = milvusService.searchByCondition(
            MilvusConstants.COLLECTION_NAME_QUESTIONS_ENGLISH_GZ,
            ids,
            condition
        );

        log.info("search results wrapper:{}", wrapper);
        log.info("id:{}", wrapper.getFieldWrapper(MilvusConstants.Field.ID).getFieldData());
        log.info("eigen_values:{}", wrapper.getFieldWrapper(MilvusConstants.Field.EIGENVALUES).getFieldData());

        // 另外一种解析结果的方式
        List<QueryResultsWrapper.RowRecord> rowRecords = wrapper.getRowRecords();
        for (QueryResultsWrapper.RowRecord rowRecord : rowRecords) {
            rowRecord.getFieldValues().forEach((k, v) -> {
                log.info("key:{},value:{}", k, v);
            });
        }

    }


    /**
     * 测试释放集合
     */
    @Test
    public void testReleaseCollection() {
        boolean success = milvusService.releaseCollection(MilvusConstants.COLLECTION_NAME_QUESTIONS_ENGLISH_GZ);
        System.out.println(success);
    }

}
