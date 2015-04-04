package org.vietj.vertx.eventloop;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.docgen.Source;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@Source
public class ContextPropagationFromVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    EventBus eventBus = vertx.eventBus();

    vertx.deployVerticle(new AbstractVerticle() {
      public void start() throws Exception {
        System.out.println("Starting verticle with " + Thread.currentThread());
        eventBus.send("the-address", "the-message", reply -> {
          System.out.println("Got reply on " + Thread.currentThread());
        });
      }
    });

    eventBus.consumer("the-address", msg -> {
      msg.reply("the-reply");
    });
  }
}
