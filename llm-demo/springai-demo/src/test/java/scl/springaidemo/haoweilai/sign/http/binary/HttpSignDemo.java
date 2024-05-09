package scl.springaidemo.haoweilai.sign.http.binary;

import com.tal.ailab.util.DateUtil;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import scl.springaidemo.demos.haoweilai.signature.enums.RequestMethod;
import scl.springaidemo.demos.haoweilai.signature.sign.SendSignHttp;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static scl.springaidemo.demos.haoweilai.signature.util.HttpUtil.BINARY;
import static scl.springaidemo.demos.haoweilai.signature.util.HttpUtil.INPUT_STREAM_ENTITY;


/**
 * 获取Http签名和发送http请求
 */
public class HttpSignDemo {

    public static  void sendPostOrPatchOrPut (String access_key_id, String access_key_secret, String url, RequestMethod requestMethod) throws Exception{

        //根据接口要求，填写真实URL参数。key1、key2仅做举例
        Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("key1", "value1");
        urlParams.put("key2", "value2");

        //此文件只做示例
        String filePath=System.getProperty("user.dir")+ File.separator+"src"+ File.separator+"test"+File.separator+"java"+ File.separator+"sign"+File.separator+"test.txt";
        File f = new File(filePath);
        //根据接口要求，填写实际Body参数
        Map<String, Object> bodyParams = new HashMap<>();
        bodyParams.put(INPUT_STREAM_ENTITY, new FileInputStream(f));

        //设置请求头content-type
        String contentType = BINARY;

        //当前时间（东8区）
        Date timestamp = DateUtil.getCurrentDate();

        HttpResponse httpResponse = SendSignHttp.sendRequest(
                access_key_id,
                access_key_secret,
                timestamp,
                url,
                urlParams,
                bodyParams,
                requestMethod,
                contentType);

        String resposeJson = EntityUtils.toString(httpResponse.getEntity(), Charset.defaultCharset());
        System.out.println(resposeJson);
    }

    public static  void sendGetOrDelete (String access_key_id,String access_key_secret,String url,RequestMethod requestMethod) throws Exception{
        //根据接口要求，填写真实URL参数。key1、key2仅做举例
        Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("key1", "value1");
        urlParams.put("key2", "value2");

        //设置请求头content-type
        String contentType = BINARY;

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