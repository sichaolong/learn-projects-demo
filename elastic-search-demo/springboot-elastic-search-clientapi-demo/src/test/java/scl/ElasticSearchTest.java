package scl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

/**
 * @projectName: learn-projects-demo
 * @package: scl
 * @className: ElasticSearchTest
 * @author: sichaolong
 * @description: TODO
 * @date: 2023/9/4 0:00
 * @version: 1.0
 */

@SpringBootTest
public class ElasticSearchTest {

    // URL and API key
    String serverUrl = "https://120.46.82.131:9200";
    String apiKey = "MF9IT1c0b0JFYXk3MlhCY3NxMG46TUdHeGhqMC1TT3Vydm5EQWExYTJSUQ==";

    // Create the low-level client
    RestClient restClient = RestClient
            .builder(HttpHost.create(serverUrl))
            .setDefaultHeaders(new Header[]{
                    new BasicHeader("Authorization", "ApiKey " + apiKey)
            })
            .build();

    // Create the transport with a Jackson mapper
    ElasticsearchTransport transport = new RestClientTransport(
            restClient, new JacksonJsonpMapper());

    // And create the API client
    ElasticsearchClient esClient = new ElasticsearchClient(transport);


    @Test
    public void createTest() throws IOException {

        //写法比RestHighLevelClient更加简洁
        CreateIndexResponse indexResponse = esClient.indices().create(c -> c.index("user"));
    }
}
