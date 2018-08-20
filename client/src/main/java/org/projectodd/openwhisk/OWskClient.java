package org.projectodd.openwhisk;

import org.projectodd.openwhisk.invoker.ApiClient;
import org.projectodd.openwhisk.invoker.JSON;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static okio.ByteString.encodeString;

public class OWskClient {
    private ApiClient client;
    private Configuration configuration;

    public OWskClient() {
        configure(Configuration.load());
    }

    public void configure(final Configuration configuration) {
        this.configuration = configuration;
        client = new ApiClient();
        client.setBasePath(format("https://%s:%s/api/v1", configuration.getHost(), configuration.getPort()));

        final JSON json = client.getJSON();
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().create());

        client.setVerifyingSsl(!configuration.isInsecure());
        if(getConfiguration().getAuth()!= null) {
            client.addDefaultHeader("Authorization", "Basic " + encodeString(getConfiguration().getAuth(), ISO_8859_1).base64());
        }
        client.setUserAgent("Incubating Apache OpenWhisk Java client");
        client.setDebugging(configuration.isDebugging());
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    ApiClient getClient() {
        return client;
    }

    public Actions actions() {
        return new Actions(this);
    }
}
