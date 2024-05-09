package scl.springaidemo.demos.haoweilai.signature.util;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class HttpUtil {

	/*系统编码*/
	private static final String CHARSET = "UTF-8";

	public static final String APPLICATION_JSON = "application/json";
	public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
	//application/octet-stream 相当于postman  binary
	public static final String BINARY = "application/octet-stream";
	public static final String INPUT_STREAM_ENTITY = "inputStreamEntity";
	public static final String MULTIPART_FORM_DATA = "multipart/form-data";
	public static final String MULTIPART_ENTITY_BUILDER = "multipartEntityBuilder";

	public static HttpResponse post(
			String url,Map<String, Object> param,
			String contentType)
			throws Exception{

		HttpPost post = new HttpPost(url);
		post.setEntity(getHttpEntity(param,contentType));
		HttpClient client = HttpClients.createDefault();
		HttpResponse result = client.execute(post);
		return result;
	}

	public static HttpResponse get(String url) throws Exception {
		HttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet(url);
		HttpResponse result = client.execute(get);
		return result;
	}

	public static HttpResponse delete(String url) throws Exception {
		HttpClient client = HttpClients.createDefault();
		HttpDelete delete = new HttpDelete(url);
		HttpResponse result = client.execute(delete);
		return result;
	}

	public static HttpResponse patch(
			String url,
			Map<String, Object> param,
			String contentType) throws Exception {

		HttpPatch patch = new HttpPatch(url);
		patch.setEntity(getHttpEntity(param,contentType));
		HttpClient client = HttpClients.createDefault();
		HttpResponse result = client.execute(patch);
		return result;
	}

	public static HttpResponse put(
			String url,
			Map<String, Object> param,
			String contentType) throws Exception {

		HttpPut put = new HttpPut(url);
		put.setEntity(getHttpEntity(param,contentType));
		HttpClient client = HttpClients.createDefault();
		HttpResponse result = client.execute(put);
		return result;
	}

	public static HttpEntity getHttpEntity(
			Map<String, Object> param,
			String contentType) throws Exception{

		if(APPLICATION_X_WWW_FORM_URLENCODED.equals(contentType)){
			ArrayList<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
			if(param != null){
				param.forEach((key, value) -> list.add(new BasicNameValuePair(key,  new Gson().toJson(value))));
			}
			return new UrlEncodedFormEntity(list,CHARSET);
		}else if(MULTIPART_FORM_DATA.equals(contentType)){
			MultipartEntityBuilder builder = (MultipartEntityBuilder)param.get(MULTIPART_ENTITY_BUILDER);
			return builder.build();
		}else if(BINARY.equals(contentType)){
			InputStream instream = (InputStream)param.get(INPUT_STREAM_ENTITY);
			InputStreamEntity streamEntity = new InputStreamEntity(instream);
			streamEntity.setContentEncoding(CHARSET);
			streamEntity.setContentType(contentType);
			return streamEntity;
		}else{
			String jsonParam = new Gson().toJson(param);
			StringEntity stringEntity = new StringEntity(jsonParam,CHARSET);
			stringEntity.setContentEncoding(CHARSET);
			stringEntity.setContentType(contentType);
			return stringEntity;
		}
	}

}
