package org.projectodd.openwhisk;

import org.projectodd.openwhisk.invoker.ApiClient;

import javax.net.ssl.TrustManager;
import java.security.KeyManagementException;

import static java.lang.String.format;
import static okhttp3.internal.Util.ISO_8859_1;
import static okio.ByteString.encodeString;

public class OWskClient {
    private ApiClient client;
    private Configuration configuration;

    public OWskClient() {
        configure(Configuration.load());
    }

    public void configure(final Configuration configuration) {
        try {
            this.configuration = configuration;
            client = new ApiClient();
            client.setBasePath(format("https://%s:%s/api/v1", configuration.getHost(), configuration.getPort()));
            if(configuration.isInsecure()) {
                client.getHttpClient().getSslContext()
                      .init(null, new TrustManager[]{new MyX509TrustManager()}, new java.security.SecureRandom());
            }
            if(getConfiguration().getAuth()!= null) {
                client.addDefaultHeader("Authorization", "Basic " + encodeString(getConfiguration().getAuth(), ISO_8859_1).base64());
            }
        } catch (KeyManagementException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    Configuration getConfiguration() {
        return configuration;
    }

    public ApiClient getClient() {
        return client;
    }

    public Actions actions() {
        return new Actions(this);
    }

}
