package scl.langchain4j.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author sichaolong
 * @createdate 2024/4/19 16:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class KnowledgeBaseItem {


    /**
     * 试题ID
     */
    private String qid;

    /**
     * 试题题干（纯文本）
     */

    private String stem;

    /**
     * 试题答案（纯文本）
     */

    private String answer;

    /**
     * 试题解析（纯文本）
     */

    private String explanation;

    /**
     * 试题课程ID
     */

    private Integer courseId;

    /**
     * 试题题型ID
     */

    private String typeId;

}
