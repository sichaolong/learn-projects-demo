package scl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import scl.langchain4j.pojo.KnowledgeBaseItem;
import scl.langchain4j.rag.KnowledgeBaseService;
import scl.pojo.Pagination;
import scl.pojo.PublishedQuestion;
import scl.pojo.QuestionSearchParams;
import scl.solr.SolrService;
import scl.utils.QmlTextParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author sichaolong
 * @createdate 2024/4/20 14:53
 * <p>
 * 构建本地知识库
 */

@SpringBootTest
@Slf4j
public class KnowledgeBaseServiceTest {

    @Autowired
    KnowledgeBaseService knowledgeBaseService;

    @Autowired
    SolrService solrService;


    /**
     * 测试 文档切分并且保存到Milvus本地知识库
     */
    @Test
    public void testIngestDocumentAndStoreKnowledgeBase() {
        KnowledgeBaseItem item = new KnowledgeBaseItem();
        item.setQid("9999999999999");
        item.setStem("你是谁？");
        item.setAnswer("你是我，我是他，你是他，那我是谁？");
        item.setExplanation("66666");
        knowledgeBaseService.embeddingAndStore(item);
    }


    /**
     * 测试 构建Milvus本地知识库，从solr数据源头查询数据，然后数据切分、embedding保存
     */
    @Test
    public void testBuildKnowledgeBase() throws InterruptedException {

        // 高中英语
        Integer courseId = 28;
        // 单选题
        String questionTypeId = "2803";

        Integer rows = 1000;
        Integer page = 1;
        List<KnowledgeBaseItem> knowledgeBaseItems = getKnowledgeBaseItems(courseId, questionTypeId,rows,page);
        log.info("从solr查询的试题数据量：{}", knowledgeBaseItems.size());
        log.info("第一条数据示例：{}", JSON.toJSONString(knowledgeBaseItems.get(0)));

        List<List<KnowledgeBaseItem>> partition = ListUtils.partition(knowledgeBaseItems, 100);
        for (int i = 0; i < partition.size(); i++) {
            log.info("当前 embedding 批次：{}", i + 1);
            knowledgeBaseService.embeddingAndStore(partition.get(i));
            log.info("当前 embedding ：{} 批次结束，休息5s", i + 1);
            Thread.sleep(5000);

        }

    }

    private List<KnowledgeBaseItem> getKnowledgeBaseItems(Integer courseId, String questionTypeId,Integer rows,Integer page) {
        QuestionSearchParams solrSearchParams = new QuestionSearchParams();
        solrSearchParams.setRows(rows);
        solrSearchParams.setCourseIds(Arrays.asList(courseId));
        solrSearchParams.setTypeId(questionTypeId);
        solrSearchParams.setPage(page);
        Pagination<PublishedQuestion> pageData = solrService.getPublishedQuestionsWithPagination(solrSearchParams);
        List<PublishedQuestion> publishedQuestionList = pageData.getItems();
        List<KnowledgeBaseItem> itemList = new ArrayList<>();
        for (PublishedQuestion publishedQuestion : publishedQuestionList) {
            KnowledgeBaseItem item = new KnowledgeBaseItem();
            item.setQid(publishedQuestion.getId());
            item.setStem(QmlTextParser.parseText(publishedQuestion.getTextStem()));
            item.setAnswer(QmlTextParser.parseText(publishedQuestion.getAnswer()));
            item.setExplanation(QmlTextParser.parseText(publishedQuestion.getExplanation()));
            item.setCourseId(publishedQuestion.getCourseId());
            item.setTypeId(publishedQuestion.getTypeId());
            itemList.add(item);
        }
        return itemList;

    }
}
