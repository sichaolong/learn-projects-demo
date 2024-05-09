package scl.springaidemo.demos.haoweilai;

import lombok.Builder;
import lombok.Data;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;

import java.util.List;
import java.util.Map;

/**
 * @author sichaolong
 * @createdate 2024/5/9 14:25
 */
@Data
@Builder
public class HaoweilaiMessage implements Message {

    private String content;

    private String role;

    @Override
    public List<Media> getMedia() {
        return null;
    }

    @Override
    public Map<String, Object> getProperties() {
        return null;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.fromValue(role);
    }
}
