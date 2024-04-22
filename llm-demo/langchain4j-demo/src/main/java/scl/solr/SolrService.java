package scl.solr;

import scl.pojo.Pagination;
import scl.pojo.PublishedQuestion;
import scl.pojo.QuestionSearchParams;

/**
 * @author sichaolong
 * @createdate 2024/4/18 15:41
 */
public interface SolrService {

    Pagination<PublishedQuestion> getPublishedQuestionsWithPagination(QuestionSearchParams params);

}
