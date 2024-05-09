package scl.springaidemo.demos.haoweilai.signature.sign;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import scl.springaidemo.demos.haoweilai.signature.enums.RequestMethod;
import scl.springaidemo.demos.haoweilai.signature.util.HttpUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import static scl.springaidemo.demos.haoweilai.signature.util.HttpUtil.APPLICATION_X_WWW_FORM_URLENCODED;


public class SignatureSDK {

    private static final String CHARSET_UTF8 = "utf8";
    private static final String REQUEST_BODY = "request_body";

    /**
     * 获取签名
     * @param parameter
     * @param accessKeySecret
     * @return
     * @throws Exception
     */
    private static String generate(Map<String, Object> parameter,String accessKeySecret) throws Exception {
        String signString = generateSignString(parameter);
        byte[] signBytes = hmacSHA1Signature(accessKeySecret+"&", signString);
        String signature = newStringByBase64(signBytes);
        return signature;
    }

    /**
     * 对parameter进行排序
     * @param params
     * @return
     * @throws IOException
     */
    private static String generateSignString(Map<String, Object> params) throws IOException {
        TreeMap<String, Object> sortParams = new TreeMap<String, Object>();
        sortParams.putAll(params);
        String canonicalizedQueryString = generateQueryString(sortParams);
        return canonicalizedQueryString;
    }

    /**
     * 对params进行format
     * @param params
     * @return
     */
    private static String generateQueryString(Map<String, Object> params) {
        List<String> tempParams = new ArrayList<>(params.size());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object val = entry.getValue();
            StringBuilder sb = new StringBuilder()
                    .append(entry.getKey())
                    .append("=")
                    .append(val instanceof String ? val : new Gson().toJson(val));
            tempParams.add(sb.toString());
        }
        return StringUtils.join(tempParams,"&");
    }

    /**
     * 计算签名
     * @param secret
     * @param baseString
     * @return
     * @throws Exception
     */
    private static byte[] hmacSHA1Signature(String secret, String baseString)throws Exception {
        if (StringUtils.isEmpty(secret)) {
            throw new IOException("secret can not be empty");
        }

        if (StringUtils.isEmpty(baseString)) {
            return null;
        }
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(CHARSET_UTF8), CHARSET_UTF8);
        mac.init(keySpec);
        return mac.doFinal(baseString.getBytes(CHARSET_UTF8));
    }

    private static String newStringByBase64(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * URL参数format
     * @param params URL参数
     * @return
     * @throws IOException
     */
    public static String urlParamsFormat(Map<String, Object> params){
        if(params == null){
            return null;
        }
        return generateQueryString(params);
    }


    /**
     * 生成签名
     * @param access_key_id
     * @param access_key_secret
     * @param timestamp
     * @param urlParams
     * @param bodyParams
     * @param requestMethod
     * @param contentType
     * @throws Exception
     */
    public static void getSignature(
            String access_key_id,
            String access_key_secret,
            Date timestamp,
            Map<String, Object> urlParams,
            Map<String, Object> bodyParams,
            RequestMethod requestMethod,
            String contentType) throws Exception{

        if(StringUtils.isEmpty(access_key_id)){
            throw new Exception("参数access_key_id不能为空");
        }
        if(StringUtils.isEmpty(access_key_secret)){
            throw new Exception("参数access_key_secret不能为空");
        }
        if(timestamp == null){
            throw new Exception("参数timestamp不能为空");
        }
        if(requestMethod == null){
            throw new Exception("参数requestMethod不能为空");
        }
        if(urlParams == null){
            throw new Exception("参数urlParams不能为null,会带回签名，至少做初始化");
        }

        if(bodyParams == null){
            bodyParams = new HashMap<>();
        }
        urlParams.put("access_key_id", access_key_id);

        String signature_nonce = UUID.randomUUID().toString();
        urlParams.put("signature_nonce", signature_nonce);

        String timestampStr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(timestamp);
        urlParams.put("timestamp", timestampStr);

        Map<String,Object> signParams = new HashMap<>();
        signParams.putAll(urlParams);

        if((requestMethod.equals(RequestMethod.POST)
                || requestMethod.equals(RequestMethod.PATCH)
                || requestMethod.equals(RequestMethod.PUT))
                &&(HttpUtil.APPLICATION_JSON.equals(contentType)
                || APPLICATION_X_WWW_FORM_URLENCODED.equals(contentType))){

            if(StringUtils.isEmpty(contentType)){
                throw new Exception("参数contentType不能为空");
            }
            if(APPLICATION_X_WWW_FORM_URLENCODED.equals(contentType)){
                ArrayList<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
                bodyParams.forEach((key, value) -> list.add(new BasicNameValuePair(key,  new Gson().toJson(value))));
                //按application/x-www-form-urlencoded格式化request body
                String requestBodyStr = URLEncodedUtils.format(list, CHARSET_UTF8);
                if(StringUtils.isNotEmpty(requestBodyStr)){
                    signParams.put(REQUEST_BODY,requestBodyStr);
                }
            }else{
                signParams.put(REQUEST_BODY,bodyParams);
            }
        }
        String sign = generate(signParams, access_key_secret);
        sign = URLEncoder.encode(sign,CHARSET_UTF8);
        urlParams.put("signature",sign);
    }

}