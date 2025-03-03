package simhash;

/**
 * @author sichaolong
 * @createdate 2024/11/11 13:39
 */
public interface WordWeightStrategy {
    /**
 * 获得分词权重
 *
 * @param word   分词
 * @param nature 词性
 */
    int getWordWeight(String word, String nature);

}
