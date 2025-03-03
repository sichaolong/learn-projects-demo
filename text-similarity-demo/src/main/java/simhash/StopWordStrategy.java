package simhash;

/**
 * @author sichaolong
 * @createdate 2024/11/11 13:38
 * 停用词策略:计算文章指纹除了上述步骤的分词与计算分词Hash值外，对分词后的数据还可以进行一些额外的处理，
 * 比如忽略一些语义上没什么意义的分词、出现频率过高的分词，针对不同的分词设置不同的权重等等。
 * 上述示例代码中已添加了相关处理逻辑，涉及到的辅助接口与实现可参考本节后续示例代码。
 */
/**
 *
 */
public interface StopWordStrategy {
    /**
     * 是否停用词
     *
     * @param word   分词
     * @param nature 词性
     */
    boolean isStopWord(String word, String nature);
}
