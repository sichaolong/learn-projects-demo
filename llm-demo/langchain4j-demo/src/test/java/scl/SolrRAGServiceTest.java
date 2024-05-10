package scl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import scl.langchain4j.constants.LLMConstants;
import scl.langchain4j.constants.MilvusConstants;
import scl.langchain4j.rag.milvus.MilvusRAGService;
import scl.langchain4j.rag.solr.SolrRAGService;
import scl.pojo.Pagination;
import scl.pojo.PublishedQuestion;
import scl.pojo.QuestionEvaluation;
import scl.pojo.QuestionSearchParams;
import scl.solr.SolrService;
import scl.utils.qml.QmlTextParser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sichaolong
 * @createdate 2024/4/30 17:30
 */

@Slf4j
@SpringBootTest
public class SolrRAGServiceTest {

    @Autowired
    SolrRAGService ragService;

    @Test
    public void testRetrieveAndCreatePrompt() {
        String question = "Where shall we go for the holiday, to the park or to the cinema?";
        Map<String,String> conditionMaps = new HashMap<>();
        conditionMaps.put("courseId", "3");
        conditionMaps.put("typeId", "030602");
        Prompt prompt = ragService.retrieveAndCreatePrompt(conditionMaps, question, 3, 0.6d);
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
        Map<String, String> conditionMaps = Maps.newHashMap(MilvusConstants.Field.QUESTION_COURSEID, "28");

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
        conditionMaps.put(MilvusConstants.Field.QUESTION_COURSEID, "3");
        conditionMaps.put(MilvusConstants.Field.QUESTION_TYPEID, "030602");
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
     * 从solr选取试题，对比RAG正确率
     */

    @Autowired
    SolrService solrService;


    /**
     * 选取测试集合
     * @return
     */
    public List<PublishedQuestion> getQuestions(Integer courseId,String typeId,Integer page,Integer pageSize){
        QuestionSearchParams params = new QuestionSearchParams();
        params.setCourseIds(Arrays.asList(courseId));
        params.setTypeId(typeId);
        params.setPage(page);
        params.setRows(pageSize);
        Pagination<PublishedQuestion> pageData = solrService.getPublishedQuestionsWithPagination(params);
        List<PublishedQuestion> questions = pageData.getItems();
        log.info("随机选取测试的试题数量：{}",questions.size());
        return questions;

    }

    /**
     * 从solr选取测试集，测评LLM 、LLM + RAG 的解题效果。
     * @throws InterruptedException
     * @throws IOException
     */
    @Test
    public void testEvaluation() throws InterruptedException, IOException {

        String typeId = "2803";
        Integer courseId = 28;
        Integer page = 8;
        Integer pageSize = 500;

        List<PublishedQuestion> questions = getQuestions(courseId,typeId,page,pageSize);

        FileUtils.writeStringToFile(new File("./result-json.txt"),"[\n", StandardCharsets.UTF_8,true);

        for (int i = 0; i < questions.size(); i++) {
            PublishedQuestion item = questions.get(i);

            log.info("++++++++++++++++++++++ 当前第：{}个试题: {}",i+1, item.getTextStem());
            QuestionEvaluation temp = QuestionEvaluation.builder()
                .textStem(item.getTextStem())
                .id(item.getId())
                .stem(item.getStem())
                .answer(item.getAnswer())
                .explanation(item.getExplanation())
            .build();


            log.info("================= LLM开始解答{}...",i+1);
            Pair<String, Response<AiMessage>> responsePair1 = ragService.ask(item.getTextStem(), LLMConstants.ModelKey.AZURE_BASE_4);
            Response<AiMessage> ar1 = responsePair1.getRight();
            String llmAnswerExp = ar1.content().text();
            temp.setLlmAnswerExp(llmAnswerExp);
            log.info("================= LLM解答 {} 结束...",i+1);


            log.info("----------------- LLM + RAG 开始解答...");
            Map<String,String> conditionMaps = new HashMap<>();
            conditionMaps.put(MilvusConstants.Field.QUESTION_COURSEID, String.valueOf(courseId));
            conditionMaps.put(MilvusConstants.Field.QUESTION_TYPEID, typeId);
            Pair<String, Response<AiMessage>> ragResponsePair2 = ragService.retrieveAndAsk(conditionMaps,item.getTextStem(), LLMConstants.ModelKey.AZURE_BASE_4,3,0.6d);

            String ragLlmDetailInfo = ragResponsePair2.getLeft();
            String ragAnswerExp = ragResponsePair2.getRight().content().text();
            temp.setRagAnswerExp(ragAnswerExp);
            temp.setRagDetailInfo(ragLlmDetailInfo);

            log.info("llm + rag response:\n\n{}", ragAnswerExp);
            log.info("------------------ LLM + RAG 解答结束...");

            Thread.sleep(6000);
            String json = JSON.toJSONString(temp);
            log.info("result json:{}",json);
            FileUtils.writeStringToFile(new File("./result-json.txt"),String.format("%s,\n",json), StandardCharsets.UTF_8,true);

        }

        FileUtils.writeStringToFile(new File("./result-json.txt"),"]", StandardCharsets.UTF_8,true);
    }


    /**
     * 统计正确率
     */
    @Test
    public void statisticalAccuracy() throws IOException {
        // 读取JSON文件
        String json = FileUtils.readFileToString(new File("./result-json.txt"), StandardCharsets.UTF_8);

        Pattern pattern = Pattern.compile("【答案】：(.*?)【解析】：");


        List<QuestionEvaluation> questionEvaluations = JSON.parseArray(json, QuestionEvaluation.class);
        for (QuestionEvaluation questionEvaluation : questionEvaluations) {

            log.info("id:{}", questionEvaluation.getId());
            String llmAnswerExp = questionEvaluation.getLlmAnswerExp();
            Matcher matcher1 = pattern.matcher(llmAnswerExp);
            if (matcher1.find()) {
                log.info("llm answer:{}", matcher1.group(1));
            }
            String ragAnswerExp = questionEvaluation.getRagAnswerExp();
            Matcher matcher2 = pattern.matcher(ragAnswerExp);
            if (matcher2.find()) {
                log.info("rag llm answer:{}", matcher2.group(1));
            }


            // 正确答案
            String answer = questionEvaluation.getAnswer();
            log.info("正确答案：{}",QmlTextParser.parseText(answer));
        }
    }
}
