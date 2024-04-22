package scl.solr.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.apache.solr.client.solrj.SolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

/**
 * @author sichaolong
 * @createdate 2024/4/18 15:42
 */
@Service
@Data
public class SolrClientConfig {

    @Value("${solr.mode}")
    private String solrMode;

    @Value("${solr.zkHost:}")
    private String zkHost;
    @Value("${solr.baseSolrUrl}")
    private String baseSolrUrl;
    @Value("${solr.username}")
    private String username;
    @Value("${solr.password}")
    private String password;
    @Value("${solr.timeout:30000}")
    private int timeout;

    private SolrClient solrClient;

    @PostConstruct
    public void init() {
        solrClient = createSolrClient();
    }
    private SolrClient createSolrClient() {
        if ("cloud".equals(solrMode)) {
            return new AuthCloudSolrClient.Builder()
                .withZkHost(zkHost)
                .withBasicAuthCredential(username, password)
                .withSocketTimeout(timeout)
                .withConnectionTimeout(timeout)
                .build();
        }
        return new AuthHttpSolrClient.Builder(baseSolrUrl)
            .withBasicAuthCredential(username, password)
            .withSocketTimeout(timeout)
            .withConnectionTimeout(timeout)
            .build();
    }
}
