package org.projectodd.openwhisk.tests;

import org.projectodd.openwhisk.ActionOptions;
import org.projectodd.openwhisk.InvokeOptions;
import org.projectodd.openwhisk.model.ActionExec.KindEnum;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class PythonActionTests extends ClientTests {

    public static final String ACTION_NAME = "splitsville";

    @Test
    public void delete() {
        try {
            client.actions().delete(ACTION_NAME);
        } catch (Exception ignore) {
        }
    }

    @Test(dependsOnMethods = "delete")
    public void deploy() {
        client.actions()
              .create(new ActionOptions(ACTION_NAME)
                          .kind(KindEnum.PYTHON_2)
                          .code("../functions/src/main/python/split.py"));

        final String sentence = "I'm a simple sentence.";
        final Map<String, Object> words = mapOf("words", sentence);
        final Map<String, List<String>> result = client.actions().invoke(new InvokeOptions(ACTION_NAME)
                                                                             .parameters(words)
                                                                             .blocking(true)
                                                                             .results(true));
        Assert.assertEquals(result.get("py-result"), asList(sentence.split(" ")));
    }
}
