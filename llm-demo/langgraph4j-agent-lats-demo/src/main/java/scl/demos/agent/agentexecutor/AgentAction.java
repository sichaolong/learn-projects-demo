package scl.demos.agent.agentexecutor;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors( fluent = true)
class AgentAction  {
    @NonNull
    ToolExecutionRequest toolExecutionRequest;
    String log;
}