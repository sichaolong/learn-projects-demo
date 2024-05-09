package scl.springaidemo.demos.haoweilai;

import lombok.Data;

/**
 * @author sichaolong
 * @createdate 2024/5/9 14:34
 */
@Data
public class HaoweilaiResponse {

    /**
     * {
     * "code": 20000,
     * "msg": "success",
     * "requestId": "17152362156831238135957338763264",
     * "action": "result",
     * "data": {
     * "result": "您好!今天我能为您提供什么帮助?",
     * "is_end": 1,
     * "mod": "multi-70b",
     * "prompt_tokens": 43,
     * "total_tokens": 60,
     * "completion_tokens": 17,
     * "logprobs": {
     * "top_logprobs": null
     * },
     * "total_score": 0
     * }
     * }
     */

    private int code;

    private String msg;

    private String requestId;

    private String action;

    private HaoweilaiData data;

}

@Data
class HaoweilaiData {

    private String result;
    private int is_end;
    private String mod;
    private int prompt_tokens;
    private int total_tokens;
    private int completion_tokens;
    private Object logprobs;
    private int total_score;

}
