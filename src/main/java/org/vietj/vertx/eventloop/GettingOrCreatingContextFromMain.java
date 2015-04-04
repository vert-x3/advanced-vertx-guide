package org.vietj.vertx.eventloop;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.docgen.Source;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@Source
public class GettingOrCreatingContextFromMain {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    Context context = vertx.getOrCreateContext();
    System.out.println("Current context is " + Vertx.currentContext());
  }

}
