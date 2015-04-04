package org.vietj.vertx.eventloop;

import io.vertx.core.Vertx;
import io.vertx.docgen.Source;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@Source
public class UsingEventLoopsFromHttpServers {

  private static int numberOfServerStarted;

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.runOnContext(v -> {
      vertx.createHttpServer().listen(result -> {
        // This executes in a context
        numberOfServerStarted++;
      });
      vertx.createHttpServer().listen(result -> {
        // This executes in a different context
        numberOfServerStarted++;
      });
    });
  }
}
