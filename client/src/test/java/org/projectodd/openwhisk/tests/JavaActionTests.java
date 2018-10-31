package org.projectodd.openwhisk.tests;

import org.projectodd.openwhisk.ActionOptions;
import org.projectodd.openwhisk.InvokeOptions;
import org.projectodd.openwhisk.invoker.ApiException;
import org.projectodd.openwhisk.model.ActionExec.KindEnum;
import org.projectodd.openwhisk.model.Activation;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class JavaActionTests extends ClientTests {

    public static final String CLASS_NAME = "org.projectodd.Echo";
    public static final String ACTION_NAME = "echo";

    @Test(expectedExceptions = ApiException.class)
    public void deleteNonExistent() {
        kill(ACTION_NAME);
        client.actions().delete(ACTION_NAME);
    }

    @Test
    public void delete() {
        kill(ACTION_NAME);
    }

    @Test(dependsOnMethods = "delete")
    public void create() {
        Assert.assertNotNull(client.actions().create(
            new ActionOptions(ACTION_NAME)
                .code("../functions/target/echo.jar")
                .main(CLASS_NAME)
                .kind(KindEnum.JAVA)));
    }

    @Test(dependsOnMethods = "create", expectedExceptions = ApiException.class)
    public void createDuplicate() {
        Assert.assertNotNull(client.actions().create(
            new ActionOptions(ACTION_NAME)
                .code("../functions/target/echo.jar")
                .main(CLASS_NAME)
                .kind(KindEnum.JAVA)));
    }

    @Test(dependsOnMethods = "delete")
    public void update() {
        Assert.assertNotNull(client.actions().update(
            new ActionOptions(ACTION_NAME)
                .code("../functions/target/echo.jar")
                .main(CLASS_NAME)
                .kind(KindEnum.JAVA)));
    }

    @Test(dependsOnMethods = "create")
    public void invoke() {
        final String activationId = client.actions().invoke(new InvokeOptions(ACTION_NAME)
                                                                .parameter("test", "hello"));
        Assert.assertNotNull(activationId);

        final Activation activation = client.actions().invoke(new InvokeOptions(ACTION_NAME)
                                                                  .blocking(true)
                                                                  .parameter("test", "hello"));
        Assert.assertNotNull(activation.getActivationId());
        Assert.assertNotNull(activation.getResponse());

        Map results = client.actions().invoke(new InvokeOptions(ACTION_NAME)
                                                  .blocking(true)
                                                  .results(true)
                                                  .parameter("test", "hello"));
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.get("echoed"));
    }
}
