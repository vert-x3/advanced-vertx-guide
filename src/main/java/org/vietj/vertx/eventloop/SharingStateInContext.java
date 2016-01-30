package org.vietj.vertx.eventloop;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.docgen.Source;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@Source
public class SharingStateInContext {

  public static void eventLoop(Vertx vertx) {
    vertx.deployVerticle(new AbstractVerticle() {
      int value = 0;
      @Override
      public void start() throws Exception {
        vertx.setPeriodic(100, id -> value++);
        vertx.eventBus().consumer("the-address", msg -> {
          msg.reply(value);
        });
      }
    });
  }

  public static void worker(Vertx vertx) {
    vertx.deployVerticle(new AbstractVerticle() {
      volatile int value;
      @Override
      public void start() throws Exception {
        vertx.setPeriodic(100, id -> value++);
        vertx.eventBus().consumer("the-address", msg -> {
          msg.reply(value);
        });
      }
    }, new DeploymentOptions().setWorker(true));
  }
}
