package org.vietj.vertx.eventloop;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.docgen.Source;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@Source
public class CurrentContextFromVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new AbstractVerticle() {
      public void start() throws Exception {
        System.out.println("Current context is " + Vertx.currentContext());
        System.out.println("Verticle context is " + context);
        System.exit(0);
      }
    });
  }

  public static void source() {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new AbstractVerticle() {
      public void start() throws Exception {
        System.out.println("Current context is " + Vertx.currentContext());
        System.out.println("Verticle context is " + context);
      }
    });
  }
}
