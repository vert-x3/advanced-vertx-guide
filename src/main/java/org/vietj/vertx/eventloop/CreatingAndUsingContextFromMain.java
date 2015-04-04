package org.vietj.vertx.eventloop;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.docgen.Source;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@Source
public class CreatingAndUsingContextFromMain {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    Context context = vertx.getOrCreateContext();
    context.runOnContext(v -> {
      System.out.println("Current context is " + Vertx.currentContext());
    });
  }

}
