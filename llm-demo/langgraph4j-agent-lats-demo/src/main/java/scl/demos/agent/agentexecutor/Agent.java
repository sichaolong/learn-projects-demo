package scl.demos.agent.agentexecutor;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.*;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.output.Response;
import lombok.Builder;
import lombok.Singular;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import static scl.demos.agent.graph.utils.CollectionsUtils.mapOf;

@Builder
public class Agent {
    private final  ChatLanguageModel chatLanguageModel;
    @Singular  private final List<ToolSpecification> tools;


    public Response<AiMessage> execute( String input, List<IntermediateStep> intermediateSteps ) {
        Prompt prompt = PromptTemplate.from("{{input}}")
                .apply(mapOf("input", input));
        var userMessageTemplate = prompt;

        var messages = new ArrayList<ChatMessage>();

        messages.add(new SystemMessage("You are a helpful assistant"));
        messages.add(new UserMessage(userMessageTemplate.text()));

        if (!intermediateSteps.isEmpty()) {

            List<ToolExecutionRequest> collect = intermediateSteps.stream()
                    .map(IntermediateStep::action)
                    .map(AgentAction::toolExecutionRequest)
                    .collect(Collectors.toList());
            var toolRequests = collect;

            messages.add(new AiMessage(toolRequests)); // reply with tool requests

            for (IntermediateStep step : intermediateSteps) {
                ToolExecutionRequest toolExecutionRequest = step.action().toolExecutionRequest();
                var toolRequest = toolExecutionRequest;

                messages.add(new ToolExecutionResultMessage(toolRequest.id(), toolRequest.name(), step.observation()));
            }
        }
        return chatLanguageModel.generate( messages, tools );
    }
}
