package org.projectodd.openwhisk;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClientTests {
    final OWskClient client = new OWskClient();

    public ClientTests() {
        client.configure(client.getConfiguration()
                               .from()
                               .insecure(true)
                               .build());
    }

    protected Map<String, String> mapOf(final String... strings) {
        final Map<String, String> map = new LinkedHashMap<>();
        for (int index = 0; index < strings.length - 1; index += 2) {
            map.put(strings[index], strings[index + 1]);
        }
        return map;
    }
}
