package scl.springaidemo.demos.haoweilai;

import com.azure.ai.openai.models.ChatRequestMessage;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.ai.chat.messages.ChatMessage;
import org.springframework.ai.chat.prompt.ChatOptions;

import java.util.List;


/**
 * @author sichaolong
 * @createdate 2024/5/9 10:47
 */

@Data
@ToString
@Builder
public class HaoweilaiChatOptions implements ChatOptions {

    /**
     * 学科0-9分别对应：语、数、外、物、化、生、政、
     * 其他。不传时为空字符串，走数学。
     */

    private String subject;

    /**
     * 表示单次请求多次采样，比如n=50，单次请求最多
     * 采样结果。
     * 默认值0表示走默认配置10次采样。
     * n取值范围：0 < n <= 50
     */

    private Integer n;


    /**
     * 使用多次采样，即n>1时，请设置stream为true
     */
    private boolean stream;

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
}
