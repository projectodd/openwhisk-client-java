package org.projectodd.openwhisk;

import org.projectodd.openwhisk.invoker.ApiClient;
import org.projectodd.openwhisk.invoker.JSON;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static okio.ByteString.encodeString;

import java.io.IOException;

public class OWskClient {
    private ApiClient client;
    private Configuration configuration;

    public OWskClient() throws IOException {
        configure(Configuration.load());
    }

    public void configure(final Configuration configuration) throws IOException {
        this.configuration = configuration;
        client = new ApiClient();
        client.setBasePath(format("https://%s:%s/api/v1", configuration.getHost(), configuration.getPort()));

        final JSON json = client.getJSON();
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().create());

        client.setVerifyingSsl(!configuration.isInsecure());
        if(getConfiguration().getAuth()!= null) {
            if (configuration.useOauth()) {
                OauthManager oauthManager = new OauthManager(
                        configuration.getOauthTokenUrl(),
                        configuration.getOauthTokenRequestParameters(),
                        configuration.getOauthTokenRequestHeaders());
                client.getHttpClient().interceptors().add(oauthManager);
                client.getHttpClient().setAuthenticator(oauthManager);
            } else {
                client.addDefaultHeader(
                        "Authorization",
                        "Basic " + encodeString(getConfiguration().getAuth(), ISO_8859_1)
                        .base64());
            }
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

    public Activations activations() {
        return new Activations(this);
    }
}
