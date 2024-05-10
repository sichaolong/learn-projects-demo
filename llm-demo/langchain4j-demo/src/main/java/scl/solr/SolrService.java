package scl.solr;

import org.apache.solr.client.solrj.SolrQuery;
import scl.pojo.Pagination;
import scl.pojo.PublishedQuestion;
import scl.pojo.QuestionSearchParams;

import java.util.List;

/**
 * @author sichaolong
 * @createdate 2024/4/18 15:41
 */
public interface SolrService {

    Pagination<PublishedQuestion> getPublishedQuestionsWithPagination(QuestionSearchParams params);

    <T> List<T> solrQuery(String coreName, SolrQuery query, Class<T> clazz);

    List<PublishedQuestion> getSimilarityQuestions(QuestionSearchParams params);
}
