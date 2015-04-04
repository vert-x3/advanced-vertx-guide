package org.vietj.vertx.eventloop;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.docgen.Source;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@Source
public class CreatingAnEventLoopFromHttpServer {

  public static void main(String[] args) {
    // Does not run in a context
    Vertx vertx = Vertx.vertx();
    HttpServer server = vertx.createHttpServer();
    server.listen(result -> {
      // This runs in a context created just for the purpose of this http server
    });
  }
}
