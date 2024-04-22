package scl.langchain4j.llm;

import lombok.Data;

/**
 * @author sichaolong
 * @createdate 2024/4/19 16:07
 */
@Data
public abstract class AbstractModelInfo {

    private String modelName;

    private Boolean enable;
}
