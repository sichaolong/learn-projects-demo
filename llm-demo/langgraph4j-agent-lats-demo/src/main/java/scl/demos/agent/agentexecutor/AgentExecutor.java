package scl.demos.agent.agentexecutor;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.FinishReason;

import scl.demos.agent.async.AsyncGenerator;
import scl.demos.agent.graph.GraphRepresentation;
import scl.demos.agent.graph.NodeOutput;
import scl.demos.agent.graph.StateGraph;
import scl.demos.agent.graph.state.AgentState;
import scl.demos.agent.graph.state.AppendableValue;
import scl.demos.agent.tool.ToolInfo;

import java.util.*;
import java.util.stream.Collectors;

import static scl.demos.agent.graph.StateGraph.END;
import static scl.demos.agent.graph.action.AsyncEdgeAction.edge_async;
import static scl.demos.agent.graph.action.AsyncNodeAction.node_async;
import static scl.demos.agent.graph.utils.CollectionsUtils.mapOf;

public class AgentExecutor {

    public static class State extends AgentState {

        public State(Map<String, Object> initData) {
            super(initData);
        }

        Optional<String> input() {
            return value("input");
        }
        public Optional<AgentOutcome> agentOutcome() {
            return value("agent_outcome");
        }
        public AppendableValue<IntermediateStep> intermediateSteps() {
            return appendableValue("intermediate_steps");
        }


    }

    Map<String,Object> runAgent( Agent agentRunnable, State state ) throws Exception {

        String s = state.input()
                .orElseThrow(() -> new IllegalArgumentException("no input provided!"));
        var input = s;

        var intermediateSteps = state.intermediateSteps().values();

        var response = agentRunnable.execute( input, intermediateSteps );

        if( response.finishReason() == FinishReason.TOOL_EXECUTION ) {

            List<ToolExecutionRequest> toolExecutionRequests1 = response.content().toolExecutionRequests();
            var toolExecutionRequests = toolExecutionRequests1;
            var action = new AgentAction( toolExecutionRequests.get(0), "");

            return mapOf("agent_outcome", new AgentOutcome( action, null ) );

        }
        else {
            String text = response.content().text();
            var result = text;
            var finish = new AgentFinish( mapOf("returnValues", result), result );

            return mapOf("agent_outcome", new AgentOutcome( null, finish ) );
        }

    }
    Map<String,Object> executeTools(List<ToolInfo> toolInfoList, State state ) throws Exception {

        var agentOutcome = state.agentOutcome().orElseThrow(() -> new IllegalArgumentException("no agentOutcome provided!"));

        if (agentOutcome.action() == null) {
            throw new IllegalStateException("no action provided!" );
        }

        var toolExecutionRequest = agentOutcome.action().toolExecutionRequest();

        var tool = toolInfoList.stream()
                            .filter( v -> v.specification().name().equals(toolExecutionRequest.name()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("no tool found for: " + toolExecutionRequest.name()));

        String execute = tool.executor().execute(toolExecutionRequest, null);
        var result = execute;

        return mapOf("intermediate_steps", new IntermediateStep( agentOutcome.action(), result ) );

    }

    String shouldContinue(State state) {

        if (state.agentOutcome().map(AgentOutcome::finish).isPresent()) {
            return "end";
        }
        return "continue";
    }

    public AsyncGenerator<NodeOutput<State>> execute(ChatLanguageModel chatLanguageModel, Map<String, Object> inputs, List<Object> objectsWithTools) throws Exception {


        List<ToolInfo> toolInfos = ToolInfo.fromList(objectsWithTools);
        var toolInfoList = toolInfos;

        final List<ToolSpecification> toolSpecifications = toolInfoList.stream()
                .map(ToolInfo::specification)
                .collect(Collectors.toList());

        var agentRunnable = Agent.builder()
                                .chatLanguageModel(chatLanguageModel)
                                .tools( toolSpecifications )
                                .build();

        var workflow = new StateGraph<>(State::new);

        workflow.setEntryPoint("agent");

        workflow.addNode( "agent", node_async( state ->
            runAgent(agentRunnable, state))
        );

        workflow.addNode( "action", node_async( state ->
            executeTools(toolInfoList, state))
        );

        workflow.addConditionalEdges(
                "agent",
                edge_async(this::shouldContinue),
                mapOf("continue", "action", "end", END)
        );

        workflow.addEdge("action", "agent");

        var app = workflow.compile();
        var result = app.getGraph(GraphRepresentation.Type.PLANTUML);
        System.out.println(result.getContent());
        return  app.stream( inputs );
    }
}
