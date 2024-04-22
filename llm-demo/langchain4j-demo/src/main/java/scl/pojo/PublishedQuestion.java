package scl.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;

import java.util.Date;
import java.util.List;

/**
 * @author sichaolong
 * @createdate 2024/4/18 15:48
 */

@Data
public class PublishedQuestion {
    @Field
    private String id;
    @Field
    private Integer courseId;
    @Field
    private String typeId;
    @Field
    private String paperId;
    @Field
    private String source;
    @Field
    private Integer year;
    @Field
    private Double difficulty;
    @Field
    private String stem;
    /**
     * 数学公式转成AlphaTex的纯文本题干
     */
    @Field
    private String textStem;
    @Field
    private String answer;
    @Field
    private String explanation;
    @Field
    @JSONField(format = "yyyy-MM-dd HH:mm:ss.mmm")
    private Date publishDate;
    @Field
    @JSONField(format = "yyyy-MM-dd HH:mm:ss.mmm")
    private Date createDate;

    @Field
    @JSONField(format = "yyyy-MM-dd HH:mm:ss.mmm")
    private Date updateDate;

    @Field
    @JSONField(format = "yyyy-MM-dd HH:mm:ss.mmm")
    private Date qmlUpdateDate;
    @Field
    private Boolean multiExplanation;

    private List<String> moreExplanations;
    @Field
    private List<Integer> catalogIds;
    @Field
    private List<Integer> sourceCatalogIds;
    /**
     * 章节，包含父级章节
     */
    @Field
    private List<Integer> catalogPath;
    @Field
    private List<Integer> kpointIds;
    /**
     * 知识点，包含父级知识点
     */
    @Field
    private List<Integer> kpointPath;
    @Field
    private List<Integer> primaryKPointIds;
    @Field
    private List<String> tagIds;
    @Field
    private List<Integer> bankIds;
    @Field
    private String applicationId;
    @Field
    private String sourceId;
    @Field
    private Integer paperTypeId;
    @Field
    private List<String> paperTagIds;
    @Field
    private Integer media;
    @Field
    private Integer subCourse;
    @Field
    private String optPointIds;
    /**
     * 能力维度
     */
    @Field
    private String pointAbilityIds;
    @Field
    private List<Integer> years;
    @Field
    private List<Integer> paperTypeIds;
    @Field
    private List<String> areaIds;
    @Field
    private Integer citationTimes;
    @Field
    private List<Integer> trickIds;



    /**
     * 教材版本
     */
    @Field
    private List<Integer> versionIds;

    /**
     * 课本
     */
    @Field
    private List<Integer> textBookIds;
    /**
     * 试题新鲜度
     * 值从0-100，0表示完全新题（库里没有和它相似的题），100表示绝对不是新题（库里有很多和它相似的题）
     */
    @Field
    private Integer freshScore;
    /**
     * 试题星级：1-5，5级为最高。
     */
    @Field
    private Integer star;
    /**
     * string类型。 从solr查询出来后，需要反序列化为List<EnWord>对象。
     */
    @Field
    private String enWords;
    /**
     * 单词id列表
     */
    @Field
    private List<Integer> enWordIds;
    /**
     * 单词含义列表
     */
    @Field
    private List<Integer> enWordMeaningIds;
    /**
     * 是否支持在线作答，0=不支持，1=支持
     */
    @Field
    private Integer answerScoreable;
    /**
     * 相似度，冗余数据
     */
    private Double similarity;
    /**
     * 试题完整的QSL(Json格式)，不需要支持搜索
     */
    @Field
    private String questionStructure;
    /**
     * 1=选择题，2=填空（解答题），3=小问复合题 4、小题复合题
     */
    @Field
    private Integer struct;
    /**
     * 当3、4时有效，数字代表小题或者小问数量，简单题的sqCount=0
     */
    @Field
    private Integer sqCount;
    /**
     * 选项数量
     */
    @Field
    private Integer optionCount;
    /**
     * 选择题的答案数量，用于不定项、多选、双选的筛选场景
     */
    @Field
    private Integer optionAnsCount;
    /**
     * 真实挖出的答题空即bk元素的个数
     */
    @Field
    private Integer blankCount;
    /**
     * 交互项的个数。选择题算1个交互项；如果一个题没有拆出任何空，也算1个交互项（比如简答题）；每个bk是一个交互项。复合题的交互项是每个小题中的交互项的总和。
     */
    @Field
    private Integer interactionCount;
    /**
     * 小题结构统计特征。仅当struct=3、4时有效。1= 小题（问）全部是选择题，2=小题（问）全部是填空题, 0=混合型
     */
    @Field
    private Integer sqStructStat;
    /**
     * 选择题小题数量
     */
    @Field
    private Integer choiceSqCount;
    /**
     * partial = 部分支持机阅，all=全部支持机阅，none=全部不支持机阅
     */
    @Field
    private String ansExact;
    /**
     * 是否缺少解析，1=是，0=否
     */
    @Field
    private Integer missExp;
    /**
     * //解析片段大纲:"分析","详解","点睛","原文" 等
     */
    @Field
    private List<String> expSeg;
    /**
     * 元素在试题各个部位的分布：picStem代表题干中有图片(元素数量扩展(先不实现)：picStem8代表题干中有8个图片)；
     * Stem=题干，Ans=答案，Exp=解析
     * 元素包含下面这些：pic=图片，formula=公式，table=表格，shushi=竖式，audio=音频，video=视频，pytzg=拼音田字格
     */
    @Field
    private List<String> elementsDistribution;
}