package org.projectodd.openwhisk;

import org.junit.Assert;
import org.junit.Test;
import org.projectodd.openwhisk.model.Activation;

import java.util.Map;
import java.util.TreeMap;

public class OWskClientTest {
    @Test
    public void client() {
        final OWskClient client = new OWskClient();
        client.configure(client.getConfiguration()
                               .from()
                               .insecure(true)
                               .build());
        Map<String, String> params = new TreeMap<>();
        params.put("test", "hello");

        final String activationId = client.actions().invoke("echo", params);
        Assert.assertNotNull(activationId);

        final Activation activation = client.actions().invoke("echo", params, new InvokeOptions().blocking(true));
        Assert.assertNotNull(activation.getActivationId());
        Assert.assertNotNull(activation.getResponse());

        Map results = client.actions().invoke("echo", params, new InvokeOptions().blocking(true).results(true));
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.get("echoed"));

    }
}
