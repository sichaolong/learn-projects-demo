package scl.langchain4j.rag.solr;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scl.langchain4j.llm.LLMContext;
import scl.langchain4j.rag.RAGService;
import scl.pojo.Pagination;
import scl.pojo.PublishedQuestion;
import scl.pojo.QuestionSearchParams;
import scl.solr.SolrService;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;

import static java.util.stream.Collectors.joining;
import static scl.langchain4j.constants.LLMConstants.*;


/**
 * @author sichaolong
 * @createdate 2024/4/30 11:32
 */

@Service
@Slf4j
public class SolrRAGService implements RAGService {

    private static final String KNOWLEDGE_ITEM_FORMAT = "【题干】：%s；【答案】：%s；【解析】：%s";


    @Autowired
    SolrService solrService;

    @Override
    public Pair<String, Response<AiMessage>> retrieveAndAsk(Map<String, String> conditionMaps, String question, String modelName, Integer recallMaxResults, Double recallMinScore) {

        Prompt systemPrompt = createSystemPrompt();

        long startTime = System.currentTimeMillis();
        // recall
        Prompt prompt = retrieveAndCreatePrompt(conditionMaps, question, recallMaxResults, recallMinScore);
        if (null == prompt) {
            return null;
        }
        long endTime = System.currentTimeMillis();
        log.info("recall 耗时 ：{} 毫秒", endTime - startTime);

        startTime = System.currentTimeMillis();
        Response<AiMessage> response = new LLMContext(modelName).getLLMService().chat(Arrays.asList(systemPrompt.toSystemMessage(), prompt.toUserMessage()));
        endTime = System.currentTimeMillis();
        log.info("ask llm messages：{}", Arrays.asList(systemPrompt.toSystemMessage(), prompt.toUserMessage()));
        log.info("llm answer 耗时 ：{} 毫秒", endTime - startTime);

        return new ImmutablePair<>(prompt.text(), response);
    }

    public Prompt retrieveAndCreatePrompt(Map<String, String> conditionMaps, String question, Integer recallMaxResults, Double recallMinScore) {

        // recall
        QuestionSearchParams params = new QuestionSearchParams();
        params.setStem(question);
        params.setRows(recallMaxResults);
        Pagination<PublishedQuestion> questionList = solrService.getPublishedQuestionsWithPagination(params);
        log.info("recall questions from solr:{}", questionList);


        String information = "";
        if (Objects.isNull(questionList) || CollectionUtils.isNotEmpty(questionList.getItems())) {
            // Create a prompt for the model that includes question and relevant embeddings
            information = questionList.getItems().stream()
                .map(i -> {
                    String stem = i.getStem();
                    String answer = i.getAnswer();
                    String explanation = i.getExplanation();
                    return String.format(KNOWLEDGE_ITEM_FORMAT, stem, answer, explanation);
                })
                .collect(joining("\n\n"));

            if (StringUtils.isBlank(information)) {
                return null;
            }
        }
        if (StringUtils.isEmpty(information)) {
            return PROMPT_USER_TEMPLATE.apply(Map.of("question", question));
        }
        return PROMPT_USER_RAG_TEMPLATE_3.apply(Map.of("question", question, "information", Matcher.quoteReplacement(information)));

    }

    @Override
    public Pair<String, Response<AiMessage>> ask(String question, String modelName) {
        return null;
    }


    /**
     * 创建系统提示词
     *
     * @return
     */
    public Prompt createSystemPrompt() {
        return Prompt.from(PROMPT_SYSTEM_TEMPLATE.template());
    }
}
