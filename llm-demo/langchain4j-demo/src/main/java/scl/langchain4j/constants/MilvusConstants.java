package scl.langchain4j.constants;

import io.milvus.param.IndexType;
import io.milvus.param.MetricType;

/**
 * @author sichaolong
 * @createdate 2024/4/17 15:55
 */
public class MilvusConstants {


    /**
     * 库名
     */
    public static final String DATABASE_NAME = "xkw_jcyy_qbm_llm_rag_questions";


    /**
     * 集合名称
     */

    public static final String COLLECTION_NAME_QUESTIONS_ENGLISH_GZ = "llm_rag_questions_28_2803";
    public static final String COLLECTION_NAME_QUESTIONS_ENGLISH_GZ_DESC = "用于大模型RAG增强检索链的试题平台已经生产（有答案、解析）的试题集合（高中英语）";


    /**
     * IVF_FLAT
     */
    public static final IndexType IVF_FLAT_INDEX_TYPE = IndexType.IVF_FLAT;

    /**
     * 创建IVF索引聚类数量
     * 其中IVF_FLAT、IVF_SQ8、IVF_PQ、BIN_FLAT等索引创建的时候支持 nlist，查询时候支持nporbe参数
     * - nlist 建议值为 4 × sqrt(n)，其中 n 指 segment 最多包含的 entity 条数。
     * <p>
     * questions_english_gz集合的特征向量 eigen_values 创建索引的聚类中心数量
     */
    public static final String IVF_INDEX_EXTRA_PARAM_ENGLISH_GZ = "{\"nlist\":1600}";

    /**
     * HNSW
     */
    public static final IndexType HNSW_INDEX_TYPE = IndexType.HNSW;

    /**
     * 创建HNSW索引的层数和度数，参考：https://milvus.io/docs/v2.0.x/index.md#HNSW
     * HNSW将图的每一层上的节点的最大度限制为M。此外，您还可以使用efConstruction（构建索引时）或ef（搜索目标时）指定搜索范围。
     */
    public static final String HNSW_INDEX_EXTRA_PARAM_ENGLISH_GZ = "{\"efConstruction\":64,\"M\":8}";


    /**
     * 距离度量COSINE（余轩相似度）
     */

    public static final MetricType METRIC_TYPE_ENGLISH_GZ = MetricType.COSINE;


    /**
     * 分片数量
     */
    public static final Integer SHARDS_NUM = 4;
    /**
     * 分区数量
     */
    public static final Integer PARTITION_NUM = 16;

    /**
     * 分区前缀
     */
    public static final String PARTITION_PREFIX = "shards_";
    /**
     * 特征值长度
     */
    public static final Integer FEATURE_DIM = 384;


    /**
     * 向量查询返回结果数量
     */
    public static final Integer TOP_K = 10;


    /**
     * 字段
     */
    public static class Field {
        /**
         * 主键id
         */
        public static final String ID = "id";
        public static final String ID_DESC = "主键id";


        public static final Integer ID_MAX_LENGTH = 36;



        /**
         * 试题id
         */
        public static final String QUESTION_ID = "qid";
        public static final String QUESTION_ID_DESC = "试题id";
        public static final Integer QUESTION_ID_MAX_LENGTH = 16;

        /**
         * 试题内容
         */
        public static final String QUESTION_CONTENT = "content";
        public static final String QUESTION_CONTENT_DESC = "试题内容";
        public static final Integer QUESTION_CONTENT_MAX_LENGTH = 5120;

        /**
         * 特征值
         */
        public static final String EIGENVALUES = "eigen_values";
        public static final String EIGENVALUES_DESC = "特征向量";

        /**
         * 其他数据
         */
        public static final String METADATA = "metadata";
        public static final String METADATA_DESC = "其他数据";
        public static final Integer METADATA_MAX_LENGTH = 5120;

        public static final String QUESTION_COURSEID = "courseId";
        public static final String QUESTION_COURSEID_DESC = "试题课程ID";

        public static final String QUESTION_TYPEID = "试题题型ID";
        public static final String QUESTION_TYPEID_DESC = "试题题型ID";
    }


    /**
     * 执行结果
     */
    public static final Integer TURE = 0;
    public static final Integer FALSE = 1;


}
