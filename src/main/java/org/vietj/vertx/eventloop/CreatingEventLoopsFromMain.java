package org.vietj.vertx.eventloop;

import io.vertx.core.Vertx;
import io.vertx.docgen.Source;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@Source
public class CreatingEventLoopsFromMain {

  public static void main(String[] args) {
    System.out.println(Thread.currentThread());
    Vertx vertx = Vertx.vertx();
    for (int i = 0;i < 20;i++) {
        int index = i;
        vertx.runOnContext(v -> System.out.println(index + ":" + Thread.currentThread()));
      }
  }
}
