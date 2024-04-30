package scl.langchain4j.rag;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.output.Response;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

/**
 * @author sichaolong
 * @createdate 2024/4/30 11:18
 */
public interface RAGService {

    /**
     * 召回并向LLM提问
     *
     * @param conditionMaps   query condition
     * @param question         user's question
     * @param modelName        LLM model name
     * @param recallMaxResults recall max count from knowledge base db
     * @param recallMinScore   recall min score from knowledge base db
     * @return
     */
    Pair<String, Response<AiMessage>> retrieveAndAsk(Map<String, String> conditionMaps, String question, String modelName, Integer recallMaxResults, Double recallMinScore);

    /**
     * 直接向LLM提问
     *
     * @param question
     * @param modelName
     * @return
     */

    Pair<String, Response<AiMessage>> ask(String question, String modelName);
}
