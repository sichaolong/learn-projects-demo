package simhash;

import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sichaolong
 * @createdate 2024/11/11 13:36
 */
public class SimHash {

    /**
     * 默认Hash数量
     */
    private static final int DEFAULT_HASH_COUNT = 64;
    /**
     * 超频词最大上限
     */
    private static final int WORD_OVER_COUNT = 5;

    private final int hashCount;
    private final int wordOverCount;
    private final WordWeightStrategy wordWeightStrategy;
    private final StopWordStrategy stopWordStrategy;
    private final WordHashStrategy wordHashStrategy;
    private final Tokenizer tokenizer;

    public SimHash() {
        this(DEFAULT_HASH_COUNT, WORD_OVER_COUNT, new FixedWordWeightStrategy(),
                new HanlpStopWordStrategy(), new DefaultWordHashStrategy(),
                new HanlpTokenizer());
    }

    public SimHash(int hashCount, int wordOverCount,
                   WordWeightStrategy wordWeightStrategy, StopWordStrategy stopWordStrategy,
                   WordHashStrategy wordHashStrategy, Tokenizer tokenizer) {
        this.hashCount = hashCount;
        this.wordOverCount = wordOverCount;
        this.wordWeightStrategy = wordWeightStrategy;
        this.stopWordStrategy = stopWordStrategy;
        this.wordHashStrategy = wordHashStrategy;
        this.tokenizer = tokenizer;
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add(SimHash.clearSpecialCharacters("我是蒋固金，欢迎查看我的博客"));
        list.add(SimHash.clearSpecialCharacters("我是蒋固金，欢迎查看我的博"));
        list.add(SimHash.clearSpecialCharacters("欢迎查看我的博客"));
        list.add(SimHash.clearSpecialCharacters("我是蒋固金"));
        list.add(SimHash.clearSpecialCharacters("我是"));

        String original = SimHash.clearSpecialCharacters("我是蒋固金，欢迎查看我的博客");

        SimHash simHash = new SimHash();

        BigInteger originalSimHash = simHash.simHash(original);
        // 计算相似度
        for (int i = 0; i < list.size(); i++) {
            BigInteger hash = simHash.simHash(list.get(i));
            int distance = simHash.getHammingDistance(originalSimHash, hash);
            double similar = simHash.getSimilar(originalSimHash, hash);

            System.out.println("索引：" + i + ", 汉明距离：" + distance
                    + ", 相似度：" + similar);
        }
    }


    /**
     * 获取SimHash相似度
     * @param one
     * @param two
     * @return
     */
    public double getSimilar(String one, String two) {
        if(StringUtils.isEmpty(one) && StringUtils.isEmpty(two)){
            return 1;
        }
        if(StringUtils.isEmpty(one) || StringUtils.isEmpty(two)){
            return 0;
        }
        String s1 = this.clearSpecialCharacters(one);
        String s2 = this.clearSpecialCharacters(two);

        BigInteger s1SimHash = this.simHash(s1);
        BigInteger s2SimHash = this.simHash(s2);

        // int distance = this.getHammingDistance(s1SimHash, s2SimHash);
        double similar = this.getSimilar(s1SimHash, s2SimHash);
        return similar;
    }



    /**
     * 使用SimHash算法生成文本的指纹信息
     *
     * @param content 文本内容
     */
    public BigInteger simHash(String content) {
        int[] hashArray = new int[this.hashCount];

        // 设置分词统计量
        Map<String, Integer> wordMap = new HashMap<>();

        // 对内容进行分词处理
        this.tokenizer.segment(content, (word, nature) -> {
            // 过滤停用词
            if (this.stopWordStrategy.isStopWord(word, nature)) {
                return;
            }

            // 过滤超频词
            if (wordMap.containsKey(word)) {
                Integer count = wordMap.get(word);
                if (count > this.wordOverCount) {
                    return;
                }
                wordMap.put(word, count + 1);
            } else {
                wordMap.put(word, 1);
            }

            // 计算单个分词的Hash值
            BigInteger wordHash = this.wordHashStrategy.getWordHash(word, nature, this.hashCount);
            // 设置初始权重
            int weight = this.wordWeightStrategy.getWordWeight(word, nature);

            for (int i = 0; i < this.hashCount; i++) {
                // 向量位移
                BigInteger bitMask = BigInteger.ONE.shiftLeft(i);

                // 计算所有分词的向量
                if (wordHash.and(bitMask).signum() != 0) {
                    hashArray[i] += weight;
                } else {
                    hashArray[i] -= weight;
                }
            }
        });

        // 生成指纹，降维处理
        BigInteger fingerprint = BigInteger.ZERO;
        for (int i = 0; i < this.hashCount; i++) {
            if (hashArray[i] >= 0) {
                fingerprint = fingerprint.add(BigInteger.ONE.shiftLeft(i));
            }
        }

        return fingerprint;
    }

    /**
     * 过滤特殊字符
     */
    public static String clearSpecialCharacters(String content) {
        // 将内容转换为小写
        content = StringUtils.lowerCase(content);
        // 过滤特殊字符
        String[] strings = { " ", "\n", "\r", "\t", "\\r", "\\n", "\\t",
                "&nbsp;", "&amp;", "&lt;", "&gt;", "&quot;", "&qpos;", "　" };
        for (String string : strings) {
            content = content.replaceAll(string, "");
        }
        return content;
    }

    /**
     * 获取相似度
     */
    public double getSimilar(BigInteger one, BigInteger two) {
        // 获取海明距离
        double hammingDistance = this.getHammingDistance(one, two);
        // 求得海明距离百分比
        double scale = (1 - hammingDistance / this.hashCount) * 100;
        return Double.parseDouble(String.format("%.2f", scale));
    }

    /**
     * 获取相似度
     */
    public double getSimilar(long hash1, long hash2) {
        // 获取海明距离
        double hammingDistance = this.getHammingDistance(hash1, hash2);
        // 求得海明距离百分比
        double scale = (1 - hammingDistance / 64) * 100;
        return Double.parseDouble(String.format("%.2f", scale));
    }

    /**
     * 获取海明距离
     */
    public int getHammingDistance(BigInteger hash1, BigInteger hash2) {
        // 求差集
        BigInteger subtract = BigInteger.ONE.shiftLeft(this.hashCount).subtract(BigInteger.ONE);
        // 求异或
        BigInteger xor = hash1.xor(hash2).and(subtract);

        int distance = 0;
        while (xor.signum() != 0) {
            distance += 1;
            xor = xor.and(xor.subtract(BigInteger.ONE));
        }
        return distance;
    }

    /**
     * 获取海明距离
     */
    public int getHammingDistance(long hash1, long hash2) {
        long xor = hash1 ^ hash2;
        int distance = 0;
        while (xor != 0) {
            distance++;
            xor &= xor - 1;
        }
        return distance;
    }
}
