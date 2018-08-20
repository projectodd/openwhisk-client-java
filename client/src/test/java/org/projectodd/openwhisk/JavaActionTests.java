package org.projectodd.openwhisk;

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
        try {
            client.actions().delete(ACTION_NAME);
        } catch (ApiException ignore) {
        }
        client.actions().delete(ACTION_NAME);
    }

    @Test
    public void delete() {
        try {
            client.actions().delete(ACTION_NAME);
        } catch (ApiException ignore) {
        }
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
        Map<String, Object> params = mapOf("test", "hello");

        final String activationId = client.actions().invoke(new InvokeOptions(ACTION_NAME)
                                                                .parameter("test", "hello"));
        Assert.assertNotNull(activationId);

        final Activation activation = client.actions().invoke(new InvokeOptions(ACTION_NAME)
                                                                  .blocking(true)
                                                                  .parameters(params));
        Assert.assertNotNull(activation.getActivationId());
        Assert.assertNotNull(activation.getResponse());

        Map results = client.actions().invoke(new InvokeOptions(ACTION_NAME)
                                                  .blocking(true)
                                                  .results(true)
                                                  .parameters(params));
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.get("echoed"));
    }
}
