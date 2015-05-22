package org.vietj.vertx.eventloop;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.docgen.Source;

import java.util.concurrent.atomic.AtomicInteger;

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
      AtomicInteger value = new AtomicInteger(0);
      @Override
      public void start() throws Exception {
        vertx.setPeriodic(100, id -> value.incrementAndGet());
        vertx.eventBus().consumer("the-address", msg -> {
          msg.reply(value.get());
        });
      }
    }, new DeploymentOptions().setWorker(true));
  }
}
