package scl;


import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import scl.demos.config.DotEnvConfig;
import scl.demos.agent.adaptiverag.AdaptiveRag;
import scl.demos.agent.adaptiverag.QuestionRewriter;
import scl.demos.agent.adaptiverag.QuestionRouter;
import scl.demos.agent.adaptiverag.RetrievalGrader;
import scl.demos.agent.graph.CompiledGraph;
import scl.demos.agent.graph.GraphRepresentation;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static scl.demos.agent.graph.utils.CollectionsUtils.mapOf;


public class AdaptiveRagTest {

    @BeforeAll
    public static void beforeAll() {


        DotEnvConfig.load();
    }

    String getOpenAIBaseUrl() {
        return DotEnvConfig.valueOf("OPENAI_BASE_URL")
                .orElseThrow(() -> new IllegalArgumentException("no OPENAI_BASE_URL provided!"));
    }


    @Test
    public void QuestionRewriterTest() {

        String result = QuestionRewriter.of(getOpenAIBaseUrl()).apply("agent memory");
        assertEquals("What is the role of memory in an agent's functioning?", result);
    }


    /**
     * RAG召回知识打分
     */

    @Test
    public void RetrievalGraderTest() {

        RetrievalGrader grader = RetrievalGrader.of(getOpenAIBaseUrl());
        String question = "agent memory";

        // EmbeddingSearchResult<TextSegment> relevant = chroma.search( query );
        // TODO Solr 召回
        EmbeddingSearchResult<TextSegment> relevant = null;

        List<EmbeddingMatch<TextSegment>> matches = relevant.matches();

        assertEquals(1, matches.size());

        RetrievalGrader.Score answer =
                grader.apply(RetrievalGrader.Arguments.of(question, matches.get(0).embedded().text()));

        assertEquals("no", answer.binaryScore);


    }


    @Test
    public void questionRouterTest() {

        QuestionRouter qr = QuestionRouter.of(getOpenAIBaseUrl());

        QuestionRouter.Type result = qr.apply("What are the stock options?");

        assertEquals(QuestionRouter.Type.web_search, result);

        result = qr.apply("agent memory?");

        assertEquals(QuestionRouter.Type.knowledgebase, result);
    }


    @Test
    public void getGraphTest() throws Exception {

        AdaptiveRag adaptiveRag = new AdaptiveRag(getOpenAIBaseUrl());

        CompiledGraph<AdaptiveRag.State> stateCompiledGraph = adaptiveRag.buildGraph();
        var graph = stateCompiledGraph;

        var plantUml = graph.getGraph(GraphRepresentation.Type.PLANTUML);

        System.out.println(plantUml.getContent());
    }


    @Test
    public void testAdaptiveRag() throws Exception {


        AdaptiveRag adaptiveRag = new AdaptiveRag(getOpenAIBaseUrl());

        // 创建图
        var graph = adaptiveRag.buildGraph();

//        var result = graph.stream(mapOf("question", "What player at the Bears expected to draft first in the 2024 NFL draft?"));
         var result = graph.stream( mapOf( "question", "What kind the agent memory do iu know?" ) );

        String generation = "";
        for (var r : result) {
            System.out.printf("Node: '%s':\n", r.node());

            System.out.println(r);
            generation = r.state().generation().orElse("");
        }

        System.out.println(generation);

        // generate plantuml script
        var plantUml = graph.getGraph(GraphRepresentation.Type.PLANTUML);
        System.out.println(plantUml);

    }
}
