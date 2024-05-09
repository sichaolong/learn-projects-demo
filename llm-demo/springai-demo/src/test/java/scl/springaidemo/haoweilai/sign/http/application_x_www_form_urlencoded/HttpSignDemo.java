package scl.springaidemo.haoweilai.sign.http.application_x_www_form_urlencoded;



import com.tal.ailab.util.DateUtil;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import scl.springaidemo.demos.haoweilai.signature.enums.RequestMethod;
import scl.springaidemo.demos.haoweilai.signature.sign.SendSignHttp;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static scl.springaidemo.demos.haoweilai.signature.util.HttpUtil.APPLICATION_X_WWW_FORM_URLENCODED;

/**
 * 获取Http签名和发送http请求
 */
public class HttpSignDemo {

    public static  void sendPostOrPatchOrPut (String access_key_id,String access_key_secret,String url,RequestMethod requestMethod) throws Exception{
        //根据接口要求，填写真实URL参数。key1、key2仅做举例
        Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("key1", "value1");
        urlParams.put("key2", "value2");

        //根据接口要求，填写实际Body参数。key1、key2仅做举例
        Map<String, Object> bodyParams = new HashMap<>();
        bodyParams.put("key3","value1");
        bodyParams.put("key4","value2");

        //设置请求头content-type
        String contentType = APPLICATION_X_WWW_FORM_URLENCODED;

        //当前时间（东8区）
        Date timestamp = DateUtil.getCurrentDate();

        /**
         * 获取签名鉴权，并发送请求
         */
        HttpResponse httpResponse = SendSignHttp.sendRequest(
                access_key_id,
                access_key_secret,
                timestamp,
                url,
                urlParams,
                bodyParams,
                requestMethod,
                contentType);
        //响应结果httpResponse
        String resposeJson = EntityUtils.toString(httpResponse.getEntity(), Charset.defaultCharset());
        System.out.println(resposeJson);
    }

    public static void sendGetOrDelete (String access_key_id, String access_key_secret, String url, RequestMethod requestMethod) throws Exception{
        //根据接口要求，填写真实URL参数。key1、key2仅做举例
        Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("key1", "value1");
        urlParams.put("key2", "value2");

        //设置请求头content-type
        String contentType = APPLICATION_X_WWW_FORM_URLENCODED;

        //当前时间（东8区）
        Date timestamp = DateUtil.getCurrentDate();

        /**
         * 获取签名鉴权，并发送请求
         */
        HttpResponse httpResponse = SendSignHttp.sendRequest(
                access_key_id,
                access_key_secret,
                timestamp,
                url,
                urlParams,
                null,
                requestMethod,
                contentType);
        //响应结果httpResponse
        String resposeJson = EntityUtils.toString(httpResponse.getEntity(), Charset.defaultCharset());
        System.out.println(resposeJson);
    }

}