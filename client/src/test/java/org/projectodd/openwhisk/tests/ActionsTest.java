package org.projectodd.openwhisk.tests;

import org.projectodd.openwhisk.ActionOptions;
import org.projectodd.openwhisk.model.Action;
import org.projectodd.openwhisk.model.ActionExec.KindEnum;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ActionsTest extends ClientTests {

    private static final String ACTION_NAME = "get-test";

    @Override
    void delete() {
        System.out.println("ActionsTest.delete");
        kill(ACTION_NAME);
    }

    @Test
    public void testGet() {
        try {
            client.actions()
                  .create(new ActionOptions(ACTION_NAME)
                              .kind(KindEnum.NODEJS_6)
                              .code("../functions/src/main/js/split.js"));

            final Action action = client.actions().get(ACTION_NAME, false);
            Assert.assertNotNull(action);
            Assert.assertEquals(action.getNamespace(), "whisk.system");
            Assert.assertEquals(action.getName(), ACTION_NAME);
            Assert.assertNull(action.getExec().getCode());
            Assert.assertNotNull(client.actions()
                                       .get(ACTION_NAME, true)
                                       .getExec()
                                       .getCode());
        } finally {
            kill(ACTION_NAME);
        }

    }

    @Test
    public void testList() {
    }
}