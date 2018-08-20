package org.projectodd.openwhisk.tests;

import org.junit.After;
import org.projectodd.openwhisk.ActionOptions;
import org.projectodd.openwhisk.model.ActionExec.KindEnum;
import org.testng.annotations.Test;

public class WebActionTests extends ClientTests {

    public static final String ACTION_NAME = "web-action";

    @After
    public void cleanUp() {
        delete();
    }

    @Test
    public void delete() {
        try {
            client.actions().delete(ACTION_NAME);
        } catch (Exception ignore) {
        }
    }

    @Test(dependsOnMethods = "delete")
    public void createWebAction() {
        client.actions().create(new ActionOptions(ACTION_NAME)
                                    .kind(KindEnum.NODEJS_6)
                                    .code("../functions/src/main/js/split.js")
                                    .web(true)
                                    .webSecure(true));
    }
}
