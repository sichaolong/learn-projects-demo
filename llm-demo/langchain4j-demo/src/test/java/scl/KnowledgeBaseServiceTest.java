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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author sichaolong
 * @createdate 2024/4/20 14:53
 *
 * 构建本地知识库
 */

@SpringBootTest
@Slf4j
public class KnowledgeBaseServiceTest {

    @Autowired
    KnowledgeBaseService knowledgeBaseService;

    @Autowired
    SolrService solrService;

    @Test
    public void testIngestDocument() {

        KnowledgeBaseItem item = new KnowledgeBaseItem();
        item.setQid("9999999999999");
        item.setStem("你是谁？");
        item.setAnswer("你是我，我是他，你是他，那我是谁");
        item.setExplanation("66666");

        knowledgeBaseService.embeddingAndStore(item);
    }


    /**
     * 测试从solr数据源头查询数据，然后embedding构建本地知识库
     */
    @Test
    public void testBuildKnowledgeBase() throws InterruptedException {

        List<KnowledgeBaseItem> knowledgeBaseItems = getKnowledgeBaseItems();
        log.info("从solr查询的试题数据量：{}",knowledgeBaseItems.size());
        log.info("第一条数据：{}", JSON.toJSONString(knowledgeBaseItems.get(0)));
        List<List<KnowledgeBaseItem>> partition = ListUtils.partition(knowledgeBaseItems, 100);
        for (int i = 0; i < partition.size(); i++) {
            log.info("当前 embedding 批次：{}",i+1);
            knowledgeBaseService.embeddingAndStore(knowledgeBaseItems);
            log.info("当前 embedding ：{} 批次结束，休息5s",i+1);
            Thread.sleep(5000);

        }

    }

    private List<KnowledgeBaseItem> getKnowledgeBaseItems() {
        QuestionSearchParams solrSearchParams = new QuestionSearchParams();
        solrSearchParams.setRows(1000);
        solrSearchParams.setCourseIds(Arrays.asList(28));
        solrSearchParams.setPage(1);
        Pagination<PublishedQuestion> pageData = solrService.getPublishedQuestionsWithPagination(solrSearchParams);
        List<PublishedQuestion> publishedQuestionList = pageData.getItems();
        List<KnowledgeBaseItem> itemList = new ArrayList<>();
        for (PublishedQuestion publishedQuestion : publishedQuestionList) {
            KnowledgeBaseItem item = new KnowledgeBaseItem();
            item.setQid(publishedQuestion.getId());
            item.setStem(publishedQuestion.getTextStem());
            item.setAnswer(publishedQuestion.getAnswer());
            item.setExplanation(publishedQuestion.getExplanation());
            itemList.add(item);
        }
        return itemList;

    }
}
