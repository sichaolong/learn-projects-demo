package scl.demos.agent.adaptiverag;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import lombok.extern.slf4j.Slf4j;

import scl.demos.agent.graph.CompiledGraph;
import scl.demos.agent.graph.StateGraph;
import scl.demos.agent.graph.state.AgentState;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static scl.demos.agent.graph.StateGraph.END;
import static scl.demos.agent.graph.action.AsyncEdgeAction.edge_async;
import static scl.demos.agent.graph.action.AsyncNodeAction.node_async;
import static scl.demos.agent.graph.utils.CollectionsUtils.mapOf;

@Slf4j(topic = "AdaptiveRag")
public class AdaptiveRag {

    /**
     * Represents the state of our graph.
     * Attributes:
     * question: question
     * generation: LLM generation
     * documents: list of documents
     */
    public static class State extends AgentState {

        public State(Map<String, Object> initData) {
            super(initData);
        }

        public String question() {
            Optional<String> result = value("question");
            return result.orElseThrow(() -> new IllegalStateException("question is not set!"));
        }

        public Optional<String> generation() {
            return value("generation");

        }

        public List<String> documents() {
            Optional<List<String>> result = value("documents");
            return result.orElse(emptyList());
        }

    }

    private final String openAIBaseUrl;


    public AdaptiveRag(String openAIBaseUrl) {
        Objects.requireNonNull(openAIBaseUrl, "no OPENAI BASEURL provided!");
        this.openAIBaseUrl = openAIBaseUrl;
    }

    /**
     * Node: Retrieve documents
     *
     * @param state The current graph state
     * @return New key added to state, documents, that contains retrieved documents
     */
    /**
     * Node: Retrieve documents
     * @param state The current graph state
     * @return New key added to state, documents, that contains retrieved documents
     */
    private Map<String,Object> retrieve( State state ) {
        log.debug("---RETRIEVE---");

        String question = state.question();

//        EmbeddingSearchResult<TextSegment> relevant = this.chroma.search( question );
//
//        List<String> documents = relevant.matches().stream()
//                .map( m -> m.embedded().text() )
//                .collect(Collectors.toList());

        return mapOf( "documents", Arrays.asList("答案是：sichaolong") , "question", question );
    }

    /**
     * Node: Generate answer
     *
     * @param state The current graph state
     * @return New key added to state, generation, that contains LLM generation
     */
    private Map<String,Object> generate( State state ) {
        log.debug("---GENERATE---");

        String question = state.question();
        List<String> documents = state.documents();

        String generation = Generation.of(openAIBaseUrl).apply(question, documents); // service

        return mapOf("generation", generation);
    }

    /**
     * Node: Determines whether the retrieved documents are relevant to the question.
     * @param state  The current graph state
     * @return Updates documents key with only filtered relevant documents
     */
    private Map<String,Object> gradeDocuments( State state ) {
        log.debug("---CHECK DOCUMENT RELEVANCE TO QUESTION---");

        String question = state.question();

        List<String> documents = state.documents();

        final RetrievalGrader grader = RetrievalGrader.of( openAIBaseUrl );

        List<String> filteredDocs =  documents.stream()
                .filter( d -> {
                    var score = grader.apply( RetrievalGrader.Arguments.of(question, d ));
                    boolean relevant = score.binaryScore.equals("yes");
                    if( relevant ) {
                        log.debug("---GRADE: DOCUMENT RELEVANT---");
                    }
                    else {
                        log.debug("---GRADE: DOCUMENT NOT RELEVANT---");
                    }
                    return relevant;
                })
                .collect(Collectors.toList());

        return mapOf( "documents", filteredDocs);
    }

    /**
     * Node: Transform the query to produce a better question.
     * @param state  The current graph state
     * @return Updates question key with a re-phrased question
     */
    private Map<String,Object> transformQuery( State state ) {
        log.debug("---TRANSFORM QUERY---");

        String question = state.question();

        String betterQuestion = QuestionRewriter.of( openAIBaseUrl ).apply( question );

        return mapOf( "question", betterQuestion );
    }

    /**
     * Node: Web search based on the re-phrased question.
     * @param state  The current graph state
     * @return Updates documents key with appended web results
     */
    private Map<String,Object> webSearch( State state ) {
        log.debug("---WEB SEARCH---");

        String question = state.question();


        return mapOf( "documents", Arrays.asList("{}") );
    }

    /**
     * Edge: Route question to web search or RAG.
     * @param state The current graph state
     * @return Next node to call
     */
    private String routeQuestion( State state  ) {
        log.debug("---ROUTE QUESTION---");

        String question = state.question();

        var source = QuestionRouter.of( openAIBaseUrl ).apply( question );
        if( source == QuestionRouter.Type.web_search ) {
            log.debug("---ROUTE QUESTION TO WEB SEARCH---");
        }
        else {
            log.debug("---ROUTE QUESTION TO RAG---");
        }
        return source.name();
    }

    /**
     * Edge: Determines whether to generate an answer, or re-generate a question.
     * @param state The current graph state
     * @return Binary decision for next node to call
     */
    private String decideToGenerate( State state  ) {
        log.debug("---ASSESS GRADED DOCUMENTS---");
        List<String> documents = state.documents();

        if(documents.isEmpty()) {
            log.debug("---DECISION: ALL DOCUMENTS ARE NOT RELEVANT TO QUESTION, TRANSFORM QUERY---");
            return "transform_query";
        }
        log.debug( "---DECISION: GENERATE---" );
        return "generate";
    }

    /**
     * Edge: Determines whether the generation is grounded in the document and answers question.
     * @param state The current graph state
     * @return Decision for next node to call
     */
    private String gradeGeneration_v_documentsAndQuestion( State state ) {
        log.debug("---CHECK HALLUCINATIONS---");

        String question = state.question();
        List<String> documents = state.documents();
        String generation = state.generation()
                .orElseThrow( () -> new IllegalStateException( "generation is not set!" ) );


        HallucinationGrader.Score score = HallucinationGrader.of( openAIBaseUrl )
                .apply( HallucinationGrader.Arguments.of(documents, generation));

        if(Objects.equals(score.binaryScore, "yes")) {
            log.debug( "---DECISION: GENERATION IS GROUNDED IN DOCUMENTS---" );
            log.debug("---GRADE GENERATION vs QUESTION---");
            AnswerGrader.Score score2 = AnswerGrader.of( openAIBaseUrl )
                    .apply( AnswerGrader.Arguments.of(question, generation) );
            if( Objects.equals( score2.binaryScore, "yes") ) {
                log.debug( "---DECISION: GENERATION ADDRESSES QUESTION---" );
                return "useful";
            }

            log.debug("---DECISION: GENERATION DOES NOT ADDRESS QUESTION---");
            return "not useful";
        }

        log.debug( "---DECISION: GENERATION IS NOT GROUNDED IN DOCUMENTS, RE-TRY---" );
        return "not supported";
    }

    public CompiledGraph<State> buildGraph() throws Exception {
        var workflow = new StateGraph<>(State::new);

        // Define the nodes
        workflow.addNode("web_search", node_async(this::webSearch) );  // web search
        workflow.addNode("retrieve", node_async(this::retrieve) );  // retrieve
        workflow.addNode("grade_documents",  node_async(this::gradeDocuments) );  // grade documents
        workflow.addNode("generate", node_async(this::generate) );  // generatae
        workflow.addNode("transform_query", node_async(this::transformQuery));  // transform_query

        // Build graph
        workflow.setConditionalEntryPoint(
                edge_async(this::routeQuestion),
                mapOf(
                        "web_search", "web_search",
                        "knowledgebase", "retrieve"
                ));

        workflow.addEdge("web_search", "generate");
        workflow.addEdge("retrieve", "grade_documents");

        workflow.addConditionalEdges(
                "grade_documents",
                edge_async(this::decideToGenerate),
                mapOf(
                        "transform_query","transform_query",
                        "generate", "generate"
                ));
        workflow.addEdge("transform_query", "retrieve");
        workflow.addConditionalEdges(
                "generate",
                edge_async(this::gradeGeneration_v_documentsAndQuestion),
                mapOf(
                        "not supported", "generate",
                        "useful", END,
                        "not useful", "transform_query"
                ));

        return workflow.compile();
    }

}

