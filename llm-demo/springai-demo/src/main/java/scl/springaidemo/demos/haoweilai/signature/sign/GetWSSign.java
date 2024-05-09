package scl.springaidemo.demos.haoweilai.signature.sign;

import scl.springaidemo.demos.haoweilai.signature.enums.RequestMethod;

import java.util.Date;
import java.util.Map;
import static scl.springaidemo.demos.haoweilai.signature.util.HttpUtil.APPLICATION_JSON;

public class GetWSSign {

    /**
     *
     * 获取WS签名鉴权
     * @param access_key_id
     * @param access_key_secret
     * @param timestamp
     * @param urlParams
     * @return
     * @throws Exception
     */
    public static String getSign (
            String url,
            String access_key_id,
            String access_key_secret,
            Date timestamp,
            Map<String, Object> urlParams
    ) throws Exception{

        SignatureSDK.getSignature(
                access_key_id,
                access_key_secret,
                timestamp,
                urlParams,
                null,
                RequestMethod.GET,
                APPLICATION_JSON);
        //签名鉴权参数放在URL中
        return url + "?" + SignatureSDK.urlParamsFormat(urlParams);
    }

}
