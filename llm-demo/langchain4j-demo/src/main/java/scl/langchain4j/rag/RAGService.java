package scl.langchain4j.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scl.langchain4j.store.CustomMilvusEmbeddingStore;
import scl.langchain4j.llm.LLMContext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static java.util.stream.Collectors.joining;
import static scl.langchain4j.constants.LLMConstants.*;


/**
 * @author sichaolong
 * @createdate 2024/4/19 11:50
 */

@Service
@Slf4j
public class RAGService {


    @Autowired
    CustomMilvusEmbeddingStore milvusEmbeddingStore;

    @Autowired
    EmbeddingStoreIngestor embeddingStoreIngestor;

    @Autowired
    EmbeddingModel embeddingModel;


    /**
     * 对文档切块并向量化
     *
     * @param document 知识库文档
     */
    public void ingestDocument(Document document) {
        embeddingStoreIngestor.ingest(document);
    }


    public void ingestDocuments(List<Document> documentList) {
        embeddingStoreIngestor.ingest(documentList);
    }

    /**
     * Retrieve documents and create prompt
     *
     * @param queryCondition Query condition
     * @param question       User's question
     * @param recallMaxResults recall max count from knowledge base db
     * @param recallMinScore   recall min score from knowledge base db
     * @return Document in the vector db
     */
    public Prompt retrieveAndCreatePrompt(Map<String, String> queryCondition, String question, Integer recallMaxResults, Double recallMinScore) {
        // Embed the question
        Embedding questionEmbedding = embeddingModel.embed(question).content();

        // Find relevant embeddings in embedding store by semantic similarity
        // You can play with parameters below to find a sweet spot for your specific use case
        if (recallMaxResults == null) {
            recallMaxResults = 3;
        }
        if (recallMinScore == null) {
            recallMinScore = 0.6;
        }
        List<EmbeddingMatch<TextSegment>> relevantEmbeddings = milvusEmbeddingStore.findRelevant(questionEmbedding, recallMaxResults, recallMinScore);

        for (EmbeddingMatch<TextSegment> relevantEmbedding : relevantEmbeddings) {
            log.info("--------------------------- recall data item -----------------------------\n");
            log.info("Relevant embedding: {}", relevantEmbedding.embedded().text());
            log.info("Score: {}", relevantEmbedding.score());
            log.info("embeddingId: {}", relevantEmbedding.embeddingId());
        }

        // TODO filter by condition
        // Create a prompt for the model that includes question and relevant embeddings
        String information = relevantEmbeddings.stream()
            .map(match -> match.embedded().text())
            .collect(joining("\n\n"));

        if (StringUtils.isBlank(information)) {
            return null;
        }
        return PROMPT_USER_RAG_TEMPLATE.apply(Map.of("question", question, "information", Matcher.quoteReplacement(information)));
    }


    /**
     * 召回并向LLM提问
     *
     * @param queryCondition   query condition
     * @param question         user's question
     * @param modelName        LLM model name
     * @param recallMaxResults recall max count from knowledge base db
     * @param recallMinScore   recall min score from knowledge base db
     * @return
     */
    public Pair<String, Response<AiMessage>> retrieveAndAsk(Map<String, String> queryCondition, String question, String modelName, Integer recallMaxResults, Double recallMinScore) {
        Prompt systemPrompt = createSystemPrompt();

        long startTime = System.currentTimeMillis();
        // recall
        Prompt prompt = retrieveAndCreatePrompt(queryCondition, question, recallMaxResults, recallMinScore);
        if (null == prompt) {
            return null;
        }
        long endTime = System.currentTimeMillis();
        log.info("recall 耗时 ：{} 毫秒",endTime - startTime);

        startTime = System.currentTimeMillis();
        Response<AiMessage> response = new LLMContext(modelName).getLLMService().chat(Arrays.asList(systemPrompt.toSystemMessage(), prompt.toUserMessage()));
        endTime = System.currentTimeMillis();
        log.info("ask llm messages：{}",Arrays.asList(systemPrompt.toSystemMessage(), prompt.toUserMessage()));
        log.info("llm answer 耗时 ：{} 毫秒",endTime - startTime);


        return new ImmutablePair<>(prompt.text(), response);
    }


    /**
     * 创建系统提示词
     * @return
     */
    private Prompt createSystemPrompt() {
        return Prompt.from(PROMPT_SYSTEM_TEMPLATE.template());
    }


    /**
     * 直接向LLM提问
     * @param question
     * @param modelName
     * @return
     */

    public Pair<String, Response<AiMessage>> ask(String question, String modelName) {
        Prompt systemPrompt = createSystemPrompt();
        long startTime = System.currentTimeMillis();
        Prompt prompt = PROMPT_USER_TEMPLATE.apply(Map.of("question", question));
        Response<AiMessage> response = new LLMContext(modelName).getLLMService().chat(Arrays.asList(systemPrompt.toSystemMessage(),prompt.toUserMessage()));
        long endTime = System.currentTimeMillis();
        log.info("ask llm messages :{}",Arrays.asList(systemPrompt.toSystemMessage(),prompt.toUserMessage()));
        log.info("llm answer 耗时 ：{} 毫秒",endTime - startTime);
        return new ImmutablePair<>(prompt.text(), response);
    }



}
