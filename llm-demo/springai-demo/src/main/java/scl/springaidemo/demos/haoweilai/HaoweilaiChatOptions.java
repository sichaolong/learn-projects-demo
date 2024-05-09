package scl.springaidemo.demos.haoweilai;

import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallingOptions;

import java.util.List;
import java.util.Set;

/**
 * @author sichaolong
 * @createdate 2024/5/8 16:34
 */
public class HaoweilaiChatOptions implements FunctionCallingOptions, ChatOptions {
    @Override
    public Float getTemperature() {
        return null;
    }

    @Override
    public Float getTopP() {
        return null;
    }

    @Override
    public Integer getTopK() {
        return null;
    }

    @Override
    public List<FunctionCallback> getFunctionCallbacks() {
        return null;
    }

    @Override
    public void setFunctionCallbacks(List<FunctionCallback> functionCallbacks) {

    }

    @Override
    public Set<String> getFunctions() {
        return null;
    }

    @Override
    public void setFunctions(Set<String> functions) {

    }
}
