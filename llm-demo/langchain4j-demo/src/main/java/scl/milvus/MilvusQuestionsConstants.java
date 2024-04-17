package scl.milvus;

/**
 * @author sichaolong
 * @createdate 2024/4/17 15:55
 */
public class MilvusQuestionsConstants {


    /**
     * 库名
     */
    public static final String DATABASE_NAME = "xkw_jcyy_qbm_questions";


    /**
     * 集合名称
     */

    public static final String COLLECTION_NAME_QUESTIONS_ENGLISH_GZ = "questions_english_gz";
    public static final String COLLECTION_NAME_QUESTIONS_ENGLISH_GZ_DESC = "试题平台已经生产（有答案、解析）的高中英语试题集合";
    /**
     * 分片数量
     */
    public static final Integer SHARDS_NUM = 8;
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
    public static final Integer FEATURE_DIM = 128;

    /**
     * 字段
     */
    public static class Field {
        /**
         * 主键id
         */
        public static final String ID = "id";
        public static final String ID_DESC = "主键id";

        /**
         * 试题id
         */
        public static final String QUESTION_ID = "qid";
        public static final String QUESTION_ID_DESC = "试题id";
        public static final Integer QUESTION_ID_MAX_LENGTH = 16;
        /**
         * 特征值
         */
        public static final String EIGENVALUES = "eigen_values";
        public static final String EIGENVALUES_DESC = "特征向量";
    }


    /**
     * 执行结果
     */
    public static final Integer TURE = 0;
    public static final Integer FALSE = 1;



}
