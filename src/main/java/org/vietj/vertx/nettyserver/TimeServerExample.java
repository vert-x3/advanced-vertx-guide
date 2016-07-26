package org.vietj.vertx.nettyserver;

import io.vertx.core.Vertx;
import io.vertx.docgen.Source;

public class TimeServerExample {

  public static void main(String[] args) {

    Vertx vertx = Vertx.vertx();

    // Create the time server
    TimeServer server = TimeServer.create(vertx);
    server.requestHandler(time -> {
      time.complete(System.currentTimeMillis());
    });

    // Start the server
    server.listen(8037, "0.0.0.0", ar -> {
      if (ar.succeeded()) {
        System.out.println("Server started");
      } else {
        ar.cause().printStackTrace();
      }
    });
  }
}
