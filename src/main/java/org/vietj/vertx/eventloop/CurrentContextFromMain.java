package org.vietj.vertx.eventloop;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.docgen.Source;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@Source
public class CurrentContextFromMain {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    System.out.println("Current context is " + Vertx.currentContext());
  }

}
