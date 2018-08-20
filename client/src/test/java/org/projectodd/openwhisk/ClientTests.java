package org.projectodd.openwhisk;

import org.testng.annotations.AfterClass;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class ClientTests {
    final OWskClient client = new OWskClient();

    public ClientTests() {
        client.configure(client.getConfiguration()
                               .from()
                               .insecure(true)
                               .build());
    }

    @AfterClass
    public void cleanUp() {
        delete();
    }

    abstract void delete();


    protected Map<String, Object> mapOf(final String... strings) {
        final Map<String, Object> map = new LinkedHashMap<>();
        for (int index = 0; index < strings.length - 1; index += 2) {
            map.put(strings[index], strings[index + 1]);
        }
        return map;
    }
}
