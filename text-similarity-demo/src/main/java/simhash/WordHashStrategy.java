package simhash;

/**
 * @author sichaolong
 * @createdate 2024/11/11 13:32
 */

import java.math.BigInteger;

/**
 * 分词Hash策略
 */
public interface WordHashStrategy {
    /**
     * 获取分词Hash
     *
     * @param word      分词
     * @param nature    词性
     * @param hashCount hash数量
     */
    BigInteger getWordHash(String word, String nature, int hashCount);
}