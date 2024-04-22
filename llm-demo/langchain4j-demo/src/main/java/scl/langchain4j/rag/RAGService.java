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

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static java.util.stream.Collectors.joining;
import static scl.langchain4j.constants.LLMConstants.PROMPT_TEMPLATE;

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
     * @param question     User's question
     * @return Document in the vector db
     */
    public Prompt retrieveAndCreatePrompt(Map<String, String> queryCondition, String question) {
        // Embed the question
        Embedding questionEmbedding = embeddingModel.embed(question).content();

        // Find relevant embeddings in embedding store by semantic similarity
        // You can play with parameters below to find a sweet spot for your specific use case
        int maxResults = 3;
        double minScore = 0.6;
        List<EmbeddingMatch<TextSegment>> relevantEmbeddings = milvusEmbeddingStore.findRelevant(questionEmbedding, maxResults, minScore);

        for (EmbeddingMatch<TextSegment> relevantEmbedding : relevantEmbeddings) {

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
        return PROMPT_TEMPLATE.apply(Map.of("question", question, "information", Matcher.quoteReplacement(information)));
    }



    /**
     * 召回并提问
     *
     * @param queryCondition query condition
     * @param question     user's question
     * @param modelName    LLM model name
     * @return
     */
    public Pair<String, Response<AiMessage>> retrieveAndAsk(Map<String, String> queryCondition, String question, String modelName) {

        Prompt prompt = retrieveAndCreatePrompt(queryCondition, question);
        if (null == prompt) {
            return null;
        }
        Response<AiMessage> response = new LLMContext(modelName).getLLMService().chat(prompt.toUserMessage());
        return new ImmutablePair<>(prompt.text(), response);
    }

    public static final String parsePromptTemplate(String question, String information) {
        return PROMPT_TEMPLATE.apply(Map.of("question", question, "information", Matcher.quoteReplacement(information))).text();
    }




}
