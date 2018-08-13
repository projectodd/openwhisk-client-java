package org.projectodd.openwhisk;

import org.projectodd.openwhisk.model.ActionExec;
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
        try {
            client.actions().delete(ACTION_NAME);
        } catch (Exception ignore) {
        }
    }

    @Test(dependsOnMethods = "delete")
    public void deploy() {
        client.actions()
              .create(ACTION_NAME, new ActionExec()
                                         .kind(KindEnum.NODEJS_6)
                                         .code("../functions/src/main/js/split.js"));

        final String sentence = "I'm a simple sentence.";
        final Map<String, String> words = mapOf("words", sentence);
        final Map<String, List<String>> result = client.actions().invoke(ACTION_NAME, words, new InvokeOptions()
                                                                                .blocking(true)
                                                                                .results(true));
        Assert.assertEquals(result.get("js-result"), asList(sentence.split(" ")));
    }
}
