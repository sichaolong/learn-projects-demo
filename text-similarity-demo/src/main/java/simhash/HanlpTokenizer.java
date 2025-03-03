package simhash;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author sichaolong
 * @createdate 2024/11/11 12:00
 */
public class HanlpTokenizer implements Tokenizer {

    @Override
    public void segment(String content, BiConsumer<String, String> consumer) {
        // 对内容进行分词处理
        List<Term> terms = StandardTokenizer.segment(content);
        for (Term term : terms) {
            // 获取分词字符串
            String word = term.word;
            // 获取分词词性
            String nature = term.nature.toString();
            consumer.accept(word, nature);
        }
    }
}
