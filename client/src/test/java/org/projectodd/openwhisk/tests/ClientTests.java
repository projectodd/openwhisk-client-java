package org.projectodd.openwhisk.tests;

import org.projectodd.openwhisk.OWskClient;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public abstract class ClientTests {
    final OWskClient client = new OWskClient();

    public ClientTests() {
        client.configure(client.getConfiguration()
                               .from()
//                               .debugging(true)
                               .insecure(true)
                               .build());
    }

    @AfterClass
    @BeforeClass
    abstract void delete();

    protected void kill(final String actionName) {
        try {
            client.actions().delete(actionName);
        } catch (Exception ignore) {
        }
    }
}
