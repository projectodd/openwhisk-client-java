package org.projectodd.openwhisk.tests;

import org.projectodd.openwhisk.ActionOptions;
import org.projectodd.openwhisk.InvokeOptions;
import org.projectodd.openwhisk.invoker.ApiException;
import org.projectodd.openwhisk.model.Action;
import org.projectodd.openwhisk.model.ActionExec.KindEnum;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class JsActionTests extends ClientTests {

    public static final String ACTION_NAME = "splitsville";

    @Test
    public void delete() {
        kill(ACTION_NAME);
    }

    @Test(dependsOnMethods = "delete")
    public void deploy() {
        client.actions()
              .create(new ActionOptions(ACTION_NAME)
                          .kind(KindEnum.NODEJS_6)
                          .code("../functions/src/main/js/split.js"));

        final String sentence = "I'm a simple sentence.";
        final Map<String, List<String>> result = client.actions().invoke(new InvokeOptions(ACTION_NAME)
                                                                             .parameter("words", sentence)
                                                                             .blocking(true)
                                                                             .results(true));
        Assert.assertEquals(result.get("js-result"), asList(sentence.split(" ")));
    }

    // TODO: the API is currently saying this auth account can not access other namespaces will explore later
    @Test(dependsOnMethods = "delete", expectedExceptions = ApiException.class)
    public void explicitNamespace() {
        final String fullName = "/custom/" + ACTION_NAME;
        kill(fullName);
        try {
            client.actions()
                  .create(new ActionOptions(fullName)
                              .kind(KindEnum.NODEJS_6)
                              .code("../functions/src/main/js/split.js"));


            final Action action = client.actions().get(fullName, false);
            Assert.assertNotNull(action);
            Assert.assertEquals(action.getNamespace(), "custom");
            Assert.assertEquals(action.getName(), ACTION_NAME);
        } finally {
            kill(fullName);
        }
    }
}
