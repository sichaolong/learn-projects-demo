package simhash;

import java.util.function.BiConsumer;

/**
 * @author sichaolong
 * @createdate 2024/11/11 11:59
 */

/**
 * 分词器
 */
public interface Tokenizer {
    /**
     * 分段
     *
     * @param content  内容
     * @param consumer 分词 词性
     */
    void segment(String content, BiConsumer<String, String> consumer);
}
