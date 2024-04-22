package scl;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import scl.langchain4j.config.LLMConfig;
import scl.langchain4j.constants.LLMConstants;
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
        String question = "Where shall we go for the holiday, to the park or to the cinema?";
        Prompt prompt = ragService.retrieveAndCreatePrompt(null,question,3,0.6d);
        log.info("rag text fill in prompt:{}",prompt.text());
    }


    /**
     * 测试直接问答
     */

    @Test
    public void testAsk(){
        String question = "给出下列答案的正确选项：\n" +
            "Where shall we go for the holiday, to the park or to the school? –_ I don’t really mind. Forget it! Why not? What’s the point? It’s up to you! A.Forget it !\n" +
            "A.Forget it ! B.Why not ? C.What’ s the point ? D.It’ s up to you!";

        question = "Adults, __overly concerned with fame and fortune, sometimes fail to see the joy in simple things __ children do not.、\n" +
            "A.unless … but\n" +
            "B.although … and\n" +
            "C.when … where\n" +
            "D.even if … unless";
        Pair<String, Response<AiMessage>> responsePair = ragService.ask(question, LLMConstants.ModelKey.QIANFAN_ERNIE_3_5_8K);

        Response<AiMessage> ar = responsePair.getRight();
        String questionText = responsePair.getLeft();

        int inputTokenCount = ar.tokenUsage().inputTokenCount();
        int outputTokenCount = ar.tokenUsage().outputTokenCount();

        log.info("question text:{}",questionText);
        log.info("ai response message:{}",ar.content());
        log.info("input token:{}",inputTokenCount);
        log.info("output token:{}",outputTokenCount);
    }

    /**
     * 测试RAG问答
     */
    @Test
    public void testRetrieveAndAsk(){
        String question = "给出下列答案的正确选项：\n" +
            "Where shall we go for the holiday, to the park or to the school? –_ I don’t really mind. Forget it! Why not? What’s the point? It’s up to you! A.Forget it !\n" +
            "A.Forget it ! B.Why not ? C.What’ s the point ? D.It’ s up to you!";
        Pair<String, Response<AiMessage>> responsePair = ragService.retrieveAndAsk(null, question, LLMConstants.ModelKey.QIANFAN_ERNIE_3_5_8K, 3, 0.6d);

        String questionText = responsePair.getLeft();
        Response<AiMessage> ar = responsePair.getRight();
        int inputTokenCount = ar.tokenUsage().inputTokenCount();
        int outputTokenCount = ar.tokenUsage().outputTokenCount();

        log.info("question text:{}",questionText);
        log.info("ai response message:{}",ar.content());
        log.info("input token:{}",inputTokenCount);
        log.info("output token:{}",outputTokenCount);

    }
}
