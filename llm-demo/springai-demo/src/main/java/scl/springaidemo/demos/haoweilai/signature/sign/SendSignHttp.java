package scl.springaidemo.demos.haoweilai.signature.sign;


import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import scl.springaidemo.demos.haoweilai.signature.enums.RequestMethod;
import scl.springaidemo.demos.haoweilai.signature.util.HttpUtil;

import java.util.Date;
import java.util.Map;


public class SendSignHttp {

    private static HttpResponse get(String url, Map<String, Object> urlParams) throws Exception{
        url = url+ SignatureSDK.urlParamsFormat(urlParams);
        HttpResponse result = HttpUtil.get(url);
        return result;
    }

    private static HttpResponse post(String url, Map<String, Object> urlParams,Map<String, Object> bodtParam,String contentType) throws Exception{
        url = url+ SignatureSDK.urlParamsFormat(urlParams);
        HttpResponse result = HttpUtil.post(url, bodtParam,contentType);
        return result;
    }

    private static HttpResponse patch(String url, Map<String, Object> urlParams,Map<String, Object> bodtParam,String contentType) throws Exception{
        url = url+ SignatureSDK.urlParamsFormat(urlParams);
        HttpResponse result = HttpUtil.patch(url, bodtParam,contentType);
        return result;
    }

    private static HttpResponse put(String url, Map<String, Object> urlParams,Map<String, Object> bodtParam,String contentType) throws Exception{
        url = url+ SignatureSDK.urlParamsFormat(urlParams);
        HttpResponse result = HttpUtil.put(url, bodtParam,contentType);
        return result;
    }

    private static HttpResponse delete(String url, Map<String, Object> urlParams) throws Exception{
        url = url+ SignatureSDK.urlParamsFormat(urlParams);
        HttpResponse result = HttpUtil.delete(url);
        return result;
    }

    /**
     * 获取签名鉴权，并发送请求
     * @param access_key_id
     * @param access_key_secret
     * @param timestamp
     * @param url
     * @param urlParams
     * @param bodyParams
     * @param requestMethod
     * @param contentType
     * @return
     * @throws Exception
     */
    public static HttpResponse sendRequest (
            String access_key_id,
            String access_key_secret,
            Date timestamp,
            String url,
            Map<String, Object> urlParams,
            Map<String, Object> bodyParams,
            RequestMethod requestMethod,
            String contentType
    ) throws Exception{

        if(StringUtils.isEmpty(url)){
            throw new Exception("参数url不能为空");
        }
        url += "?";
        SignatureSDK.getSignature(
                access_key_id,
                access_key_secret,
                timestamp,
                urlParams,
                bodyParams,
                requestMethod,
                contentType);

        HttpResponse httpResponse = null;
        switch (requestMethod){
            case GET:
                httpResponse = get(url,urlParams);
                break;
            case POST:
                httpResponse = post(url,urlParams,bodyParams,contentType);
                break;
            case PUT:
                httpResponse = put(url,urlParams,bodyParams,contentType);
                break;
            case PATCH:
                httpResponse = patch(url,urlParams,bodyParams,contentType);
                break;
            case DELETE:
                httpResponse = delete(url,urlParams);
                break;
            default:
                throw new Exception("支持[GET、POST、PUT、PATCH、DELETE]请求方式");
        }
        return httpResponse;
    }

}
