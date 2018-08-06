package org.projectodd;

import com.google.gson.JsonObject;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

/**
 * echo EchoTest
 */
public class EchoTest {
  @Test
  public void testFunction() {
    JsonObject args = new JsonObject();
    args.addProperty("name", "test");
    assertNotNull(Echo.main(args).get("echoed").getAsJsonObject().get("name"));
  }
}