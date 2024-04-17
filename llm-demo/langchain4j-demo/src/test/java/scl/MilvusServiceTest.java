package scl;

import io.milvus.grpc.DataType;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.FieldType;
import io.milvus.param.dml.InsertParam;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import scl.milvus.MilvusQuestionsConstants;
import scl.milvus.MilvusService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sichaolong
 * @createdate 2024/4/17 16:53
 */
@SpringBootTest
public class MilvusServiceTest {

    @Autowired
    MilvusService milvusService;


    /**
     * 1、测试创建集合
     */
    @Test
    public void testCreateCollection() {

        List<FieldType> fieldTypeList = new ArrayList<>();

        FieldType id = FieldType.newBuilder()
            .withName(MilvusQuestionsConstants.Field.ID)
            .withDescription(MilvusQuestionsConstants.Field.ID_DESC)
            .withDataType(DataType.Int64)
            .withPrimaryKey(true)
            .withAutoID(true)
            .build();
        FieldType qid = FieldType.newBuilder()
            .withName(MilvusQuestionsConstants.Field.QUESTION_ID)
            .withDescription(MilvusQuestionsConstants.Field.QUESTION_ID_DESC)
            .withDataType(DataType.VarChar)
            .withMaxLength(MilvusQuestionsConstants.Field.QUESTION_ID_MAX_LENGTH)
            .build();
        FieldType eigenvalues = FieldType.newBuilder()
            .withName(MilvusQuestionsConstants.Field.EIGENVALUES)
            .withDescription(MilvusQuestionsConstants.Field.EIGENVALUES_DESC)
            .withDataType(DataType.FloatVector)
            .withDimension(MilvusQuestionsConstants.FEATURE_DIM)
            .build();

        fieldTypeList.add(id);
        fieldTypeList.add(qid);
        fieldTypeList.add(eigenvalues);
        boolean success = milvusService.creatCollection(MilvusQuestionsConstants.DATABASE_NAME,
            MilvusQuestionsConstants.COLLECTION_NAME_QUESTIONS_ENGLISH_GZ,
            MilvusQuestionsConstants.COLLECTION_NAME_QUESTIONS_ENGLISH_GZ_DESC,
            fieldTypeList);
        System.out.println(success);
    }


    /**
     * 2、创建索引
     */
    @Test
    public void testCreateIndex() {
        R<RpcStatus> response = milvusService.createIndex(MilvusQuestionsConstants.DATABASE_NAME,MilvusQuestionsConstants.COLLECTION_NAME_QUESTIONS_ENGLISH_GZ, MilvusQuestionsConstants.Field.EIGENVALUES, IndexType.IVF_FLAT, MetricType.L2);
        System.out.println(response.getStatus());
    }

    /**
     * 3、加载Collection
     */
    @Test
    public void testLoadCollection() {
        boolean success = milvusService.loadCollection(MilvusQuestionsConstants.DATABASE_NAME,MilvusQuestionsConstants.COLLECTION_NAME_QUESTIONS_ENGLISH_GZ);
        System.out.println(success);
    }


    /**
     * 4、创建分区
     */
    @Test
    public void testCreatePartition() {
        boolean success = milvusService.createPartition(MilvusQuestionsConstants.COLLECTION_NAME_QUESTIONS_ENGLISH_GZ, MilvusQuestionsConstants.PARTITION_PREFIX + 1);
        System.out.println(success);
    }





    /**
     *  5、测试集合是否存在
     */
    @Test
    public void testIsExitCollection() {
        boolean exits = milvusService.isExitCollection(MilvusQuestionsConstants.DATABASE_NAME,MilvusQuestionsConstants.COLLECTION_NAME_QUESTIONS_ENGLISH_GZ);
        System.out.println(exits);
    }




    /**
     * 6、测试插入
     */
    @Test
    public void testInsert() {

        String databaseName = MilvusQuestionsConstants.DATABASE_NAME;
        String collectionName = MilvusQuestionsConstants.COLLECTION_NAME_QUESTIONS_ENGLISH_GZ;
        // 模拟数据
        List<String> questionIdList = new ArrayList<>();
        questionIdList.add("3476004932208640");
        List<List<Float>> eigenValues = new ArrayList<>();
        // 128维度
        List<Float> vectorItem = new ArrayList<>();
        for (int i = 0; i < 128; i++) {
            vectorItem.add(i * 0.1f);
        }
        eigenValues.add(vectorItem);


        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field(MilvusQuestionsConstants.Field.QUESTION_ID, questionIdList));
        fields.add(new InsertParam.Field(MilvusQuestionsConstants.Field.EIGENVALUES, eigenValues));
        boolean success = milvusService.insert(databaseName, collectionName, null, fields);
        System.out.println(success);
    }


    /**
     * 7、测试释放集合
     */
    @Test
    public void testReleaseCollection() {
        boolean success = milvusService.releaseCollection(MilvusQuestionsConstants.COLLECTION_NAME_QUESTIONS_ENGLISH_GZ);
        System.out.println(success);
    }

}
