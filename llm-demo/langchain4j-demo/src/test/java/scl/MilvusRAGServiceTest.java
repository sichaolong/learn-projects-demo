package scl;

import com.alibaba.fastjson.JSON;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;
import scl.langchain4j.constants.LLMConstants;
import scl.langchain4j.constants.MilvusConstants;
import scl.langchain4j.rag.milvus.MilvusRAGService;
import scl.pojo.Pagination;
import scl.pojo.PublishedQuestion;
import scl.pojo.QuestionEvaluation;
import scl.pojo.QuestionSearchParams;
import scl.solr.SolrService;
import scl.utils.qml.QmlTextParser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sichaolong
 * @createdate 2024/4/20 16:26
 */
@SpringBootTest
@Slf4j
public class MilvusRAGServiceTest {

    @Autowired
    MilvusRAGService ragService;


    @Autowired
    SolrService solrService;

    @Test
    public void testRetrieveAndCreatePrompt() {
        String question = "Where shall we go for the holiday, to the park or to the cinema?";
        Prompt prompt = ragService.retrieveAndCreatePrompt(null, question, 3, 0.6d);
        log.info("rag text fill in prompt:{}", prompt.text());
    }


    /**
     * 测试直接问答
     */

    @Test
    public void testAsk() {
        String question = "给出下列答案的正确选项：\n" +
            "Where shall we go for the holiday, to the park or to the school? –_ I don’t really mind. Forget it! Why not? What’s the point? It’s up to you! A.Forget it !\n" +
            "A.Forget it ! B.Why not ? C.What’ s the point ? D.It’ s up to you!";

        question = "Adults, __overly concerned with fame and fortune, sometimes fail to see the joy in simple things __ children do not.、\n" +
            "A.unless … but\n" +
            "B.although … and\n" +
            "C.when … where\n" +
            "D.even if … unless";

        question = "Children, __often fascinated by nature and animals, can learn valuable lessons __ spending time outdoors.、\n"
            + "A.unless … but\n"
            + "B.although … and\n"
            + "C.when … where\n"
            + "D.even if … unless";

        question = "Man may disappear________ other creatures who became too specialized to survive their environment.\n" +
            "A.as\n" +
            "B.just as\n" +
            "C.as if\n" +
            "D.as have";


        // 小学翻译
        question = "我和我的朋友去旅行。 I _ with my friends.";
        Pair<String, Response<AiMessage>> responsePair = ragService.ask(question, LLMConstants.ModelKey.AZURE_BASE_4);

        Response<AiMessage> ar = responsePair.getRight();
        String questionText = responsePair.getLeft();

        int inputTokenCount = ar.tokenUsage().inputTokenCount();
        int outputTokenCount = ar.tokenUsage().outputTokenCount();

        log.info("question text:{}", questionText);
        log.info("ai response message text:{}", ar.content().text());

        log.info("input token:{}", inputTokenCount);
        log.info("output token:{}", outputTokenCount);
    }

    /**
     * 测试RAG问答
     */
    @Test
    public void testRetrieveAndAsk() {

        // 高中英语单选
        String collectionName = MilvusConstants.Collection.COLLECTION_NAME_QUESTIONS_ENGLISH_28_23;
        Map<String, String> conditionMaps = Maps.newHashMap("collectionName", collectionName);

        String question = "给出下列答案的正确选项：\n" +
            "Where shall we go for the holiday, to the park or to the school? –_ I don’t really mind. Forget it! Why not? What’s the point? It’s up to you! A.Forget it !\n" +
            "A.Forget it ! B.Why not ? C.What’ s the point ? D.It’ s up to you!";

        question = "Adults, __overly concerned with fame and fortune, sometimes fail to see the joy in simple things __ children do not.、\n" +
            "A.unless … but\n" +
            "B.although … and\n" +
            "C.when … where\n" +
            "D.even if … unless";

        question = "Children, __often fascinated by nature and animals, can learn valuable lessons __ spending time outdoors.、\n"
            + "A.unless … but\n"
            + "B.although … and\n"
            + "C.when … where\n"
            + "D.even if … unless";

        question = "Man may disappear________ other creatures who became too specialized to survive their environment.\n" +
            "A.as\n" +
            "B.just as\n" +
            "C.as if\n" +
            "D.as have";

        question = "Some species may become extinct ________ other species that have adapted to their environment.\n" +
            "A.as\n" +
            "B.just as\n" +
            "C.as if\n" +
            "D.as have";

        // 小学翻译汉译英语
        collectionName = MilvusConstants.Collection.COLLECTION_NAME_QUESTIONS_ENGLISH_3_030602;
        conditionMaps.put("collectionName", collectionName);
        question = "翻译填空，我和我的朋友去旅行。 I ____ with my friends.";
        question = "翻译填空，我和我的朋友将要去旅行。 I ____ with my friends.";
        question = "翻译填空，我和我的朋友已经去旅游了 。 I ____ with my friends.";


        Pair<String, Response<AiMessage>> responsePair = ragService.retrieveAndAsk(conditionMaps, question, LLMConstants.ModelKey.QIANFAN_ERNIE_4_0_8K, 3, 0.6d);

        String questionText = responsePair.getLeft();
        Response<AiMessage> ar = responsePair.getRight();
        int inputTokenCount = ar.tokenUsage().inputTokenCount();
        int outputTokenCount = ar.tokenUsage().outputTokenCount();

        log.info("question text:{}", questionText);
        log.info("ai response message text:{}", ar.content().text());
        log.info("input token:{}", inputTokenCount);
        log.info("output token:{}", outputTokenCount);

    }


    /**
     * 根据问题id进行RAG测评（Milvus知识库）
     * @throws InterruptedException
     */


    @Test
    public void testEvaluationByQuestionIds() throws InterruptedException, IOException {

        String filePath = "./result-json-milvus-511.txt";
        FileUtils.writeStringToFile(new File(filePath),"[\n", StandardCharsets.UTF_8,true);


        List<String> questionIds = Arrays.asList(
            "1566879979937792",
            "1566879979642880",
            "1566879979577344",
            "1566879845515264",
            "1566879845285888",
            "1566879741566976",
            "1566879695732736",
            "1566879569502208",
            "1566879569485824");


        QuestionSearchParams params = new QuestionSearchParams();
        params.setQuestionIds(questionIds);
        Pagination<PublishedQuestion> pageData = solrService.getPublishedQuestionsWithPagination(params);
        List<PublishedQuestion> items = pageData.getItems();

        Map<String,String> conditionMaps = new HashMap<>();
        String collectionName = MilvusConstants.Collection.COLLECTION_NAME_QUESTIONS_ENGLISH_28_23;
        conditionMaps.put("collectionName", collectionName);

        for (PublishedQuestion item : items) {
            QuestionEvaluation evaluation = new QuestionEvaluation();

            String question = item.getStem();
            String textStem = item.getTextStem();
            String answer = QmlTextParser.parseText(item.getAnswer());

            evaluation.setStem(item.getStem());
            evaluation.setTextStem(item.getTextStem());
            evaluation.setAnswer(answer);
            evaluation.setExplanation(QmlTextParser.parseText(item.getExplanation()));
            evaluation.setId(item.getId());


            Pair<String, Response<AiMessage>> responsePair = ragService.retrieveAndAsk(conditionMaps, question, LLMConstants.ModelKey.QIANFAN_ERNIE_4_0_8K, 3, 0.6d);

            String questionText = responsePair.getLeft();
            Response<AiMessage> ar = responsePair.getRight();
            int inputTokenCount = ar.tokenUsage().inputTokenCount();
            int outputTokenCount = ar.tokenUsage().outputTokenCount();

            evaluation.setRagAnswerExp(ar.content().text());
            evaluation.setRagDetailInfo(questionText);

            log.info("question text:{}", questionText);
            log.info("ai response message text:{}", ar.content().text());
            log.info("input token:{}", inputTokenCount);
            log.info("output token:{}", outputTokenCount);

            log.info("当前试题：{}",textStem);
            log.info("正确答案：{}", answer);
            Thread.sleep(2000);

            String json = JSON.toJSONString(evaluation);
            log.info("result json:{}",json);
            FileUtils.writeStringToFile(new File(filePath),String.format("%s,\n",json), StandardCharsets.UTF_8,true);

        }

        FileUtils.writeStringToFile(new File(filePath),"]", StandardCharsets.UTF_8,true);

    }



}
