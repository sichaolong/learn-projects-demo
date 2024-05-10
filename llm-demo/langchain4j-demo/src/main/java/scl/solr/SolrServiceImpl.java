package scl.solr;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.PostConstruct;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.CommonParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import scl.pojo.Pagination;
import scl.pojo.PublishedQuestion;
import scl.pojo.QuestionSearchParams;
import scl.solr.config.SolrClientConfig;
import scl.solr.constants.SolrConstants;
import scl.utils.FieldUtil;
import scl.utils.Utilities;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sichaolong
 * @createdate 2024/4/18 15:41
 */
@Service
public class SolrServiceImpl implements SolrService {


    @Autowired
    SolrClientConfig solrClientConfig;


    Logger logger = LoggerFactory.getLogger(SolrServiceImpl.class);


    //查重库名称
    @Value("${checkingQuestionsCoreName}")
    private String checkingQuestionsCoreName;
    //发布的试题库名称
    @Value("${publishedQuestionsCoreName}")
    private String publishedQuestionsCoreName;
    //下线的试题库名称
    @Value("${offlineQuestionsCoreName}")
    private String offlineQuestionsCoreName;
    //试卷库名称(包含非P0状态的所有试卷)
    @Value("${publishedPapersCoreName}")
    private String publishedPapersCoreName;
    @Value("${paperQuestionCoreName}")
    private String paperQuestionCoreName;
    @Value("${questionQualityCoreName:}")
    private String questionQualityName;
    private static final Integer MAX_ROW = 10000;


    private SolrClient solrClient;
    @PostConstruct
    private void init() {
        solrClient = solrClientConfig.getSolrClient();
    }

    @Override
    public Pagination<PublishedQuestion> getPublishedQuestionsWithPagination(QuestionSearchParams params) {
        try {
            SolrQuery query = buildSolrQuery(params);
            if (params.getPage() != null && params.getRows() != null) {
                query.setStart((params.getPage() - 1) * params.getRows());
            }
            if (params.getRows() != null && params.getRows() > MAX_ROW) {
                throw new RuntimeException(String.format("solr最多查询%s条数据，请注意排查：" +
                    "getPublishedQuestionsWithPagination(%s)", MAX_ROW, JSON.toJSONString(params)));
            }
            query.setRows(params.getRows());
            QueryResponse response = solrClientConfig.getSolrClient().query(publishedQuestionsCoreName, query, SolrRequest.METHOD.POST);
            SolrDocumentList results = response.getResults();

            List<PublishedQuestion> questions = new ArrayList<>();
            for (SolrDocument result : results) {
                String id = result.getFieldValue("id").toString();
                String stem = result.getFieldValue("stem").toString();
                String textStem = result.getFieldValue("textStem").toString();
                Object answer = result.getFieldValue("answer");
                Object explanation = result.getFieldValue("explanation");
                if(answer == null || explanation == null) {
                    logger.info("answer or explanation is null: {}", JSON.toJSONString(result));
                    continue;
                }
                Integer courseId = (Integer) result.getFieldValue("courseId");
                String typeId = result.getFieldValue("typeId").toString();
                PublishedQuestion temp = new PublishedQuestion();
                temp.setId(id);
                temp.setStem(stem);
                temp.setAnswer(answer.toString());
                temp.setExplanation(explanation.toString());
                temp.setCourseId(courseId);
                temp.setTextStem(textStem);
                temp.setTypeId(typeId);
                questions.add(temp);
                // logger.info("查询试题信息：{}",JSON.toJSONString(temp));
            }

            Pagination<PublishedQuestion> rlt = new Pagination<>(params.getPage() == null ? 1 : params.getPage(),
                params.getRows() == null ? questions.size() : params.getRows(), (int) results.getNumFound(), questions);
            return rlt;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> List<T> solrQuery(String coreName, SolrQuery query, Class<T> clazz) {
        Pagination<T> pagination = solrQuery(coreName, query, clazz, SolrRequest.METHOD.GET);
        if (pagination != null) {
            return pagination.getItems();
        }
        return null;
    }


    /**
     * 试题题干 文本相似性搜索
     * @param params
     * @return
     */
    @Override
    public List<PublishedQuestion> getSimilarityQuestions(QuestionSearchParams params) {
        SolrQuery solrQuery = buildMoreLikeThisQuery(params);
        logger.info("solr query:{}",solrQuery);
        return solrQuery(publishedQuestionsCoreName, solrQuery, PublishedQuestion.class);
    }

    private SolrQuery buildMoreLikeThisQuery(QuestionSearchParams params) {
        SolrQuery query = new SolrQuery();
        query.setRequestHandler("/mlt");
        query.set(SolrConstants.PARAM_MLT_FL, "textStem");
        query.set(SolrConstants.PARAM_MLT_MINTF, 1);
        query.set(SolrConstants.PARAM_MLT_MINDF, 1);
        query.set(SolrConstants.PARAM_FL, "*");
        query.setRows(params.getRows());

        //重要： MoreLikeThis查询过滤条件要用fq，不能用q（因为mlt查询中q也被用来做相似度查询评分了，没有作为过滤条件）
        if (params.getCourseId() != null && params.getCourseId() > 0) {
            query.addFilterQuery(String.format("courseId:%d", params.getCourseId()));
        }


        if (StringUtils.isNotEmpty(params.getTypeId())) {
            query.addFilterQuery(String.format("typeId:%s", params.getTypeId()));
        }
        query.set("stream.body", StringUtils.isEmpty(params.getTextStem()) ? params.getStem() : params.getTextStem());
        return query;
    }

    public <T> Pagination<T> solrQuery(String coreName, SolrQuery query, Class<T> clazz, SolrRequest.METHOD method) {
        if (method == null) {
            method = SolrRequest.METHOD.GET;
        }
        QueryResponse response = null;

        try {
            response = solrClient.query(coreName, query, method);
        } catch (Exception e) {
            logger.error("error:{}",e);
        }
        SolrDocumentList docList = response.getResults();
        DocumentObjectWithEnumBinder binder = new DocumentObjectWithEnumBinder();
        List<T> results = binder.getBeans(clazz, docList);
        if (results == null || results.size() == 0) {
            return null;
        }

        // 处理highlight字段
        //        String highLightField = query.get("hl.fl");
        //        if (!StringUtils.isEmpty(highLightField)) {
        //            for (T r : results) {
        //                try {
        //                    String id = (String) FieldUtils.readField(r, "id", true);
        //                    String highLightFieldValue =
        //                        response.getHighlighting().get(id).get(highLightField).get(0);
        //
        //                    FieldUtils.writeField(r, highLightField, highLightFieldValue, true);
        //                } catch (IllegalAccessException e) {
        //                    continue;
        //                }
        //            }
        //        }

        int start = query.getStart() == null ? 0 : query.getStart();
        int pageSize = (query.getRows() == null || query.getRows().compareTo(0) <= 0) ?
            CommonParams.ROWS_DEFAULT :
            query.getRows();
        int currentPage = (start / pageSize) + 1;
        Pagination pagination =
            new Pagination(currentPage, pageSize, ((int) (response.getResults().getNumFound())), results);

        return pagination;
    }


    /**
     * 根据查询参数构建solr查询对象
     *
     * @param params 查询参数对象
     * @return
     * @throws IllegalAccessException
     */
    public SolrQuery buildSolrQuery(QuestionSearchParams params) {
        SolrQuery query = new SolrQuery();
        StringBuffer q = new StringBuffer();
        List<Field> fieldList = FieldUtil.getDeclaredFields(PublishedQuestion.class);
        appendBasicConditions(q, fieldList, params);

        if (params.getMinPublishTime() != null || params.getMaxPublishTime() != null) {
            String start = params.getMinPublishTime();
            String end = params.getMaxPublishTime();
            appendRangeConditions(q, "publishDate",
                start == null ? "*" : start, end == null ? "*" : end);
        }
        if (params.getMinDifficulty() != null || params.getMaxDifficulty() != null) {
            Double start = params.getMinDifficulty();
            Double end = params.getMaxDifficulty();
            appendRangeConditions(q, "difficulty",
                start == null ? "0" : String.valueOf(start),
                end == null ? "1" : String.valueOf(end));
        }

        if (CollectionUtils.isNotEmpty(params.getCourseIds())) {
            appendCollectionConditions(q, "courseId", params.getCourseIds(), false);
        }
        if (CollectionUtils.isNotEmpty(params.getExcludedBankIds())) {
            appendCollectionConditions(q, "bankIds", params.getExcludedBankIds(), true);
        }
        if (CollectionUtils.isNotEmpty(params.getTypeIds())) {
            appendCollectionConditions(q, "typeId", params.getTypeIds(), false);
        }

        if (CollectionUtils.isNotEmpty(params.getApplicationIds())) {
            appendCollectionConditions(q, "applicationId", params.getApplicationIds(), false);
        }
        if (CollectionUtils.isNotEmpty(params.getQuestionIds())) {
            appendCollectionConditions(q, "id", params.getQuestionIds(), false);
        }
        if (CollectionUtils.isNotEmpty(params.getPaperTypeIdQuery())) {
            appendCollectionConditions(q, "paperTypeId", params.getPaperTypeIdQuery(), false);
        }
        if (CollectionUtils.isNotEmpty(params.getYearQuery())) {
            appendCollectionConditions(q, "year", params.getYearQuery(), false);
        }
        if (CollectionUtils.isNotEmpty(params.getCatalogIds())) {
            appendCollectionConditions(q, "catalogIds", params.getCatalogIds(), false);
        }
        if (CollectionUtils.isNotEmpty(params.getKpointIds())) {
            appendCollectionConditions(q, "kpointIds", params.getKpointIds(), false);
        }
        if (StringUtils.isNotEmpty(params.getExSolrQuery())) {
            appendQueryExpression(q, params.getExSolrQuery());
        }
        if (StringUtils.isNotEmpty(params.getStartId())) {
            if (q.length() != 0 && !q.toString().endsWith("AND ")) {
                q.append(" AND ");
            }
            q.append(String.format("id:[%s TO *]", params.getStartId()));
        }

        if (q.length() > 0) {
            query.setQuery(q.toString());
        } else {
            query.setQuery("*:*");
        }
        //返回确定的字段
        if (!CollectionUtils.isEmpty(params.getSolrFields())) {
            query.setFields(params.getSolrFields().toArray(new String[0]));
        }

        if (params.getSortClauses() != null) {
            query.setSorts(params.getSortClauses());
        }
        if (StringUtils.isNotEmpty(params.getStartId())) {
            query.setSort("id", SolrQuery.ORDER.asc);
        }
        return query;
    }

    private void appendQueryExpression(StringBuffer q, String solrQueryExpression) {
        if (q.length() != 0 && !q.toString().endsWith("AND ")) {
            q.append(" AND ");
        }
        q.append(solrQueryExpression);
    }

    private void appendBasicConditions(StringBuffer q, List<Field> fieldList, Object params) {
        fieldList.forEach(field -> {
            Object o = null;
            try {
                o = FieldUtils.readField(params, field.getName(), true);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("构建solr查询字符串失败");
            }
            if (o != null && !"".equals(o.toString())) {
                String typeName = o.getClass().getSimpleName();
                switch (typeName) {
                    case "String":
                    case "Integer":
                    case "Double":
                    case "Boolean":
                    default:
                        q.append(q.length() == 0 ? "" : " AND ");
                        q.append(field.getName()).append(":").append(o);
                        break;
                    case "Date":
                        Date d = (Date) o;
                        q.append(q.length() == 0 ? "" : " AND ");
                        q.append(field.getName()).append(":").append(Utilities.convertDateToUTCString(d));
                        break;
                    case "ArrayList":
                    case "SingletonList":
                    case "JSONArray":
                        List<Integer> list = (List<Integer>) o;
                        if (!CollectionUtils.isEmpty(list)) {
                            q.append(q.length() == 0 ? "" : " AND ");
                            appendCollectionConditions(q, field.getName(), list, false);
                        }
                        break;
                }
            }
        });
    }

    private void appendRangeConditions(StringBuffer q, String fieldName, String start, String end) {
        if (q.length() != 0) {
            q.append(" AND ");
        }
        String rangeTemplate = "[%s TO %s]";
        String rangeValue = String.format(rangeTemplate, start, end);
        q.append(fieldName).append(":").append(rangeValue);
    }

    private void appendCollectionConditions(StringBuffer q, String fieldName, List<?> list, boolean inverse) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        if (q.length() != 0 && !q.toString().endsWith("AND ")) {
            q.append(" AND ");
        }
        if (inverse) {
            q.append("-");
        }
        String value =
            list.stream().map(String::valueOf).collect(Collectors.joining(" "));//多值用空格分隔等价于OR的关系， 如果要与关系请用AND

        q.append(fieldName).append(":").append("(").append(value).append(")");
    }
}
