package scl.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sichaolong
 * @createdate 2024/5/10 08:56
 *
 * 评测rag + llm 和llm的解答试题结果
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionEvaluation {

    private String id;

    private String stem;

    private String textStem;

    private String answer;

    private String explanation;

    private String llmAnswerExp;

    private String ragAnswerExp;

    private String ragDetailInfo;

}
