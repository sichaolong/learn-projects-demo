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

    private String qid;

    private String stem;

    private String answer;

    private String explanation;

    private String courseId;

    private String questionType;

}
