package scl.langchain4j.rag;

import com.alibaba.fastjson.JSON;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import scl.langchain4j.constants.MilvusConstants;
import scl.langchain4j.pojo.KnowledgeBaseItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author sichaolong
 * @createdate 2024/4/19 16:52
 * 构建Milvus本地知识向量库
 */
@Service
@Slf4j
public class KnowledgeBaseService {

    private static final String KNOWLEDGE_ITEM_FORMAT = "【题干】：%s；【答案】：%s；【解析】：%s";

    @Resource
    private RAGService ragService;


    /**
     * 数据切分、embeddiing向量化后存入Milvus本地知识库
     *
     * @param item
     * @return
     */
    public void embeddingAndStore(KnowledgeBaseItem item) {
        embeddingAndStore(Arrays.asList(item));
    }

    public void embeddingAndStore(List<KnowledgeBaseItem> itemList) {
        if (CollectionUtils.isEmpty(itemList)) {
            return;
        }
        List<Document> documentList = new ArrayList<>();
        for (KnowledgeBaseItem item : itemList) {
            // log.info("knowledge item:{}", JSON.toJSONString(item));
            String content = String.format(KNOWLEDGE_ITEM_FORMAT, item.getStem(), item.getAnswer(), item.getExplanation());
            Metadata metadata = new Metadata();
            metadata.add(MilvusConstants.Field.QUESTION_ID, item.getQid());
            metadata.add(MilvusConstants.Field.QUESTION_COURSEID, item.getCourseId());
            metadata.add(MilvusConstants.Field.QUESTION_TYPEID, item.getTypeId());
            Document document = new Document(content, metadata);
            documentList.add(document);
        }
        ragService.ingestDocuments(documentList);
    }

}
