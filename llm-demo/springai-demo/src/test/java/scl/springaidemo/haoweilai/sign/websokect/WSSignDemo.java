package scl.springaidemo.haoweilai.sign.websokect;

import com.tal.ailab.util.DateUtil;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import scl.springaidemo.demos.haoweilai.signature.sign.GetWSSign;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 获取WS签名和创建WS连接Demo
 */
public class WSSignDemo {

    private static  void createWS (String access_key_id,String access_key_secret,String url) throws Exception{

        //根据接口要求，填写真实URL参数。key1、key2仅做举例
        Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("key1", "value1");
        urlParams.put("key2", "value2");

        //获取当前时间（东8区）
        Date timestamp = DateUtil.getCurrentDate();

        url = GetWSSign.getSign(
                url,
                access_key_id,
                access_key_secret,
                timestamp,
                urlParams);

        /**
         * 使用第三方websocket客户端，可根据自己需求使用其他客户端
         * <dependency>
         *      <groupId>org.java-websocket</groupId>
         *      <artifactId>Java-WebSocket</artifactId>
         *      <version>1.3.5</version>
         * </dependency>
         */
        WebSocketClient client = new WebSocketClient(new URI(url),new Draft_6455(),null,10000) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                System.out.println("Connection is opened");
            }

            @Override
            public void onMessage(String msg) {
                System.out.println("received:"+msg);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("code："+code);
                System.out.println("reason："+reason);
                System.out.println("remote："+remote);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        };

        client.connect();
        System.out.println(client.getDraft());
        while(!client.getReadyState().equals(WebSocket.READYSTATE.OPEN)){
            System.out.println("还没有打开");
            Thread.sleep(1000);
        }
        System.out.println("");
        for (int i = 0; i < 10 ; i++){
            client.send("hello world:"+System.currentTimeMillis());
            Thread.sleep(1000);
        }
        client.close();
    }


    public static void main(String[] args) {
        try {
            /**填写自己AK
             * 获取AK教程：https://openai.100tal.com/documents/article/page?fromWhichSys=admin&id=27
             * */
            String access_key_id = "666--------------";
            String access_key_secret = "888---------------";

            //请求URL，请替换自己的真实地址
            String url = "ws://openai.100tal.com/ai---/----";

            createWS(access_key_id,access_key_secret,url);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
