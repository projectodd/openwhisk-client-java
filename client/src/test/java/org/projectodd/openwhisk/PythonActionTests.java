package org.projectodd.openwhisk;

import org.projectodd.openwhisk.model.ActionExec;
import org.projectodd.openwhisk.model.ActionExec.KindEnum;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class PythonActionTests extends ClientTests {
    @Test
    public void delete() {
        try {
            client.actions().delete("splitsville");
        } catch (Exception ignore) {
        }
    }

    @Test(dependsOnMethods = "delete")
    public void deploy() {
        client.actions()
              .create("splitsville", new ActionExec()
                                         .kind(KindEnum.PYTHON_2)
                                         .code("../functions/src/main/python/split.py"));

        final String sentence = "I'm a simple sentence.";
        final Map<String, String> words = mapOf("words", sentence);
        final Map<String, List<String>> result = client.actions().invoke("splitsville", words, new InvokeOptions()
                                                                                .blocking(true)
                                                                                .results(true));
        Assert.assertEquals(result.get("result"), asList(sentence.split(" ")));
    }
}
