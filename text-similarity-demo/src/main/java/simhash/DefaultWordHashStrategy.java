package simhash;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;


/**
 * @author sichaolong
 * @createdate 2024/11/11 13:33
 * 默认分词Hash策略
 */

public class DefaultWordHashStrategy implements WordHashStrategy {
    /**
     * 分词最小长度限制
     */
    private static final int WORD_MIN_LENGTH = 3;
    private static final BigInteger ILLEGAL_X = new BigInteger("-1");
    private static final BigInteger BIGINT_2 = new BigInteger("2");
    private static final BigInteger BIGINT_1000003 = new BigInteger("1000003");
    private static final BigInteger BIGINT_NEGATIVE_2 = new BigInteger("-2");

    private final int wordMinLength;

    public DefaultWordHashStrategy() {
        this(WORD_MIN_LENGTH);
    }

    public DefaultWordHashStrategy(int wordMinLength) {
        this.wordMinLength = wordMinLength;
    }

    @Override
    public BigInteger getWordHash(String word, String nature, int hashCount) {
        if (StringUtils.isBlank(word)) {
            // 如果分词为null，则默认hash为0
            return BigInteger.ZERO;
        }
        // 分词补位，如果过短会导致Hash算法失败
        while (word.length() < this.wordMinLength) {
            word = word + word.charAt(0);
        }

        // 分词位运算
        char[] wordArray = word.toCharArray();
        BigInteger hash = BigInteger.valueOf(wordArray[0] << 7);

        // 初始桶pow运算
        BigInteger mask = BIGINT_2.pow(hashCount).subtract(BigInteger.ONE);

        for (char item : wordArray) {
            BigInteger temp = BigInteger.valueOf(item);
            hash = hash.multiply(BIGINT_1000003).xor(temp).and(mask);
        }

        hash = hash.xor(new BigInteger(String.valueOf(word.length())));

        if (hash.equals(ILLEGAL_X)) {
            hash = BIGINT_NEGATIVE_2;
        }
        return hash;
    }
}