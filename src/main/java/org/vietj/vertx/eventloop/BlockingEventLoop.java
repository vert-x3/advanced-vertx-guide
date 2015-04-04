package org.vietj.vertx.eventloop;

import io.vertx.core.Vertx;
import io.vertx.docgen.Source;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@Source
public class BlockingEventLoop {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.setTimer(1, id -> {
      // Blocking the Vert.x event loop
      try {
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
  }
}
