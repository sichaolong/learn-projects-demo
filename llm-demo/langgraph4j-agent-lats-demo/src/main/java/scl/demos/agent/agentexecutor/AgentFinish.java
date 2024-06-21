package scl.demos.agent.agentexecutor;

import lombok.Data;
import lombok.Value;
import lombok.experimental.Accessors;

import java.util.Map;

@Value
@Accessors( fluent = true)
@Data
public class AgentFinish  {
    Map<String,Object> returnValues;
    String log;

}