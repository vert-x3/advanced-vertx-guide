package org.vietj.vertx.eventloop;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.docgen.Source;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@Source
public class GettingOrCreatingContextFromVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new AbstractVerticle() {
      public void start() throws Exception {
        Context context = vertx.getOrCreateContext();
        System.out.println(context);
        System.out.println(vertx.getOrCreateContext());
        System.exit(0);
      }
    });
  }

  public static void source() {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new AbstractVerticle() {
      public void start() throws Exception {
        Context context = vertx.getOrCreateContext();
        System.out.println(context);
        System.out.println(vertx.getOrCreateContext());
      }
    });
  }
}
