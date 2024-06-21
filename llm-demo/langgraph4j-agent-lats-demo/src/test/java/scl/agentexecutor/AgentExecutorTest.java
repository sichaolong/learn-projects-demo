package scl.agentexecutor;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import scl.demos.config.DotEnvConfig;
import scl.demos.agent.agentexecutor.AgentExecutor;
import scl.demos.agent.tool.QuestionAIAnswerTool;

import static scl.demos.agent.graph.utils.CollectionsUtils.listOf;
import static scl.demos.agent.graph.utils.CollectionsUtils.mapOf;


public class AgentExecutorTest {

    @BeforeAll
    public static void loadEnv() {
        DotEnvConfig.load();
    }

    private AgentExecutor.State executeAgent(String prompt )  throws Exception {

        Assertions.assertTrue(DotEnvConfig.valueOf("OPENAI_BASE_URL").isPresent());

        var chatLanguageModel = OpenAiChatModel.builder()
                .baseUrl( DotEnvConfig.valueOf("OPENAI_BASE_URL").get() )
                .apiKey("xxx")
                .modelName( "gpt-4o" )
                .logResponses(true)
                .maxRetries(2)
                .temperature(0.0)
                .maxTokens(2000)
                .build();


        var agentExecutor = new AgentExecutor();

        var iterator = agentExecutor.execute(
                chatLanguageModel,
                mapOf( "input", prompt ),
                listOf(new QuestionAIAnswerTool()) );

       AgentExecutor.State state = null;

        for( var i : iterator ) {
            state = i.state();
            System.out.println(i.node());
        }
        return state;

    }

    @Test
    public void testAgent() throws Exception {

        AgentExecutor.State state = executeAgent(" 分析试题知识，根据知识库知识解答试题，输出正确的答案和解析： _ good use you have made of your time to study, there is still room for improvement. A.Whatever B.However C.Though D.Whether?");
        System.out.println(state);

    }


}
