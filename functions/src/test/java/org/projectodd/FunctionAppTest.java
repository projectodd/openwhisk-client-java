package org.projectodd;

import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * echo FunctionAppTest
 */
public class FunctionAppTest {
  @Test
  public void testFunction() {
    JsonObject args = new JsonObject();
    args.addProperty("name", "test");
    assertNotNull(FunctionApp.main(args).get("echoed").getAsJsonObject().get("name"));
  }
}