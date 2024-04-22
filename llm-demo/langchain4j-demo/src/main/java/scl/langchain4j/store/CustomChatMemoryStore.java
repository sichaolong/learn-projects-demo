package scl.langchain4j.store;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.extern.slf4j.Slf4j;
import org.mapdb.DB;
import org.mapdb.DBMaker;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.data.message.ChatMessageDeserializer.messagesFromJson;
import static dev.langchain4j.data.message.ChatMessageSerializer.messagesToJson;
import static org.mapdb.Serializer.STRING;

/**
 * @author sichaolong
 * @createdate 2024/4/19 15:44
 * 使用MapDB记录历史对话记录
 */
@Slf4j
public class CustomChatMemoryStore implements ChatMemoryStore {
    public static CustomChatMemoryStore singleton;

    private final DB db = DBMaker.fileDB("chat-memory.db").transactionEnable().make();

    private final Map<String, String> map = db.hashMap("messages", STRING, STRING).createOrOpen();

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String json = map.get((String) memoryId);
        return messagesFromJson(json);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        //AiMessage in first position is not allow
        if (messages.size() > 0 && messages.get(0) instanceof AiMessage) {
            messages.remove(0);
        }
        //Filter out the available messages.(UserMessage,AiMessage)
        List<ChatMessage> availableMessage = new ArrayList<>();
        for (ChatMessage chatMessage : messages) {
            if (!(chatMessage instanceof SystemMessage)) {
                availableMessage.add(chatMessage);
            }
        }
        String json = messagesToJson(availableMessage);
        log.info("updateMessages,{}", json);
        map.put((String) memoryId, json);
        db.commit();
    }

    @Override
    public void deleteMessages(Object memoryId) {
        map.remove((String) memoryId);
        db.commit();
    }

    /**
     * 单例
     * @return
     */
    public static CustomChatMemoryStore getSingleton() {
        if (null == singleton) {
            synchronized (CustomChatMemoryStore.class) {
                if (null == singleton) {
                    singleton = new CustomChatMemoryStore();
                }
            }
        }
        return singleton;
    }
}
