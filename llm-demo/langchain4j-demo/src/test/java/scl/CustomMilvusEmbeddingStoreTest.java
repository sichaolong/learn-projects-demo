package scl;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import scl.langchain4j.rag.RAGService;
import scl.langchain4j.store.CustomMilvusEmbeddingStore;

/**
 * @author sichaolong
 * @createdate 2024/4/20 14:51
 * 测试 本地向量库组件
 */

@SpringBootTest
@Slf4j
public class CustomMilvusEmbeddingStoreTest {

    @Autowired
    CustomMilvusEmbeddingStore customMilvusEmbeddingStore;


    @Test
    public void testAdd() {
        float[] vector = new float[384];
        for (int i = 0; i < vector.length; i++) {
            vector[i] = (float) Math.random();
        }
        Embedding embedding = new Embedding(vector);
        Metadata metadata = new Metadata();
        metadata.put("qid","111");
        TextSegment textSegment = TextSegment.from("test", metadata);
        String metadataJson = customMilvusEmbeddingStore.add(embedding, textSegment);
        log.info("add success metadata:{}",metadataJson);
    }


}
