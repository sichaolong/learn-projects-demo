package scl;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import scl.pojo.Pagination;
import scl.pojo.PublishedQuestion;
import scl.pojo.QuestionSearchParams;
import scl.solr.SolrService;

import java.util.Arrays;

/**
 * @author sichaolong
 * @createdate 2024/4/18 16:09
 */
@SpringBootTest
public class SolrServiceTest {

    @Autowired
    SolrService solrService;

    @Test
    public void testSearch(){
        QuestionSearchParams params = new QuestionSearchParams();
        params.setCourseIds(Arrays.asList(28));
        params.setPage(1);
        params.setRows(10);
        Pagination<PublishedQuestion> data = solrService.getPublishedQuestionsWithPagination(params);
        System.out.println(JSON.toJSONString(data));
    }
}
