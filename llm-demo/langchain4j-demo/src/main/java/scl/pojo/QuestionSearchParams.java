package scl.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.solr.client.solrj.SolrQuery;

import java.util.List;

/**
 * @author sichaolong
 * @createdate 2024/4/18 15:48
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionSearchParams extends PublishedQuestion {
    private Integer page;//页码，从1开始
    private String cursorMark;//游标
    private Integer rows;//行数
    private String keywords;//关键字
    private List<SolrQuery.SortClause> sortClauses; //排序字段
    private Double maxDifficulty;//最大难度值
    private Double minDifficulty;//最小难度值
    private String minPublishTime;//开始发布时间
    private String maxPublishTime;//截至发布时间
    private List<Integer> excludedBankIds;//排除的子库ID集合
    private List<Integer> courseIds;//所属课程ID集合
    private List<String> typeIds;//题型ID集合
    private List<String> applicationIds;//试题来源应用ID集合
    private List<String> questionIds;
    private List<List<String>> tagList;//按分类对标签分组的集合
    /**
     * 这个字段构造的是solr查询串是year，不是years
     */
    private List<Integer> yearQuery;// 年份集合
    /**
     * 这个字段构造的solr查询串是paperTypeId，不是paperTypeIds
     */
    private List<Integer> paperTypeIdQuery; // 试卷类型集合

    /**
     * 额外的solr 查询表达式。查询的时候，直接拼接到查询条件上
     */
    private String exSolrQuery;
    /**
     * 需要返回的列：传递了该字段可以减少返回的数据项
     */
    private List<String> solrFields;
    /**
     * 开始id
     * 需要按照id递增搜索
     */
    private String startId;

    public String getStartId() {
        return startId;
    }

    public void setStartId(String startId) {
        this.startId = startId;
    }

    public List<String> getSolrFields() {
        return solrFields;
    }

    public void setSolrFields(List<String> solrFields) {
        this.solrFields = solrFields;
    }


    //表示排除这些标签的资源
    private List<String> excludeTagIds;
    //表示排除这些来源的资源
    private List<String> excludeApplicationIds;
}
