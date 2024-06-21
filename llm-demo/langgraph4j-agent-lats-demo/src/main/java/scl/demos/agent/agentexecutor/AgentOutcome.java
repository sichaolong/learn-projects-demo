package scl.demos.agent.agentexecutor;

import lombok.Data;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors( fluent = true)
@Getter
public class AgentOutcome {
    private AgentAction action;
    private AgentFinish finish;
}
