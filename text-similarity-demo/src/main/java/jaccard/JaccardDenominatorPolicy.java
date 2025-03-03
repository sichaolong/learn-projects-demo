package jaccard;

/**
 * @author sichaolong
 * @createdate 2024/11/11 14:50
 */
public enum JaccardDenominatorPolicy {
    /**
     * 最小集合容量
     */
    MIN,
    /**
     * 最大集合容量
     */
    MAX,
    /**
     * 交集容量
     */
    UNION,
    /**
     * 两个集合容量的平均数
     */
    MEAN,
    /**
     * 以左侧集合容量为准
     */
    LEFT
}