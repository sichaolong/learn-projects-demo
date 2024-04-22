package scl;

import dev.langchain4j.model.input.Prompt;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import scl.langchain4j.rag.RAGService;

/**
 * @author sichaolong
 * @createdate 2024/4/20 16:26
 */
@SpringBootTest
@Slf4j
public class RAGServiceTest {

    @Autowired
    RAGService ragService;

    @Test
    public void testRetrieveAndCreatePrompt(){
        String question = "可知如果顾客选择这些商品可以享受价格优惠、快速发货、质量保证等优惠，A项不包括在内，故选A";
        Prompt prompt = ragService.retrieveAndCreatePrompt(null,question);
        String text = prompt.text();
        log.info("text:{}",text);
    }
}
