package scl.solr.config;

import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.StringUtils;
import org.apache.solr.common.util.NamedList;

import java.io.IOException;

/**
 * @author sichaolong
 * @createdate 2024/4/18 15:43
 */
public class AuthCloudSolrClient extends CloudSolrClient {

    private String basicAuthUser;

    private String basicAuthPassword;

    public AuthCloudSolrClient(Builder builder) {
        super(builder);
        if (StringUtils.isEmpty(builder.basicAuthUser)
            || StringUtils.isEmpty(builder.basicAuthPassword)) {
            throw new RuntimeException("solr用户名和密码不能为空！");
        }

        this.basicAuthUser = builder.basicAuthUser;
        this.basicAuthPassword = builder.basicAuthPassword;
    }

    @Override
    public NamedList<Object> request(SolrRequest request, String collection) throws SolrServerException, IOException {
        request.setBasicAuthCredentials(basicAuthUser, basicAuthPassword);
        return super.request(request, collection);
    }

    public static class Builder extends CloudSolrClient.Builder {
        private String basicAuthUser;

        private String basicAuthPassword;

        public Builder withBasicAuthCredential(String user, String password) {
            this.basicAuthUser = user;
            this.basicAuthPassword = password;
            return this;
        }

        public Builder withZkHost(String zkHost) {
            super.withZkHost(zkHost);
            super.solrUrls = null;
            return this;
        }

        public AuthCloudSolrClient build() {
            return new AuthCloudSolrClient(this);
        }
    }
}
