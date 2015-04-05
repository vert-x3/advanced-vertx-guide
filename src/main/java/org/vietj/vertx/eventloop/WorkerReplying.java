package org.vietj.vertx.eventloop;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.docgen.Source;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@Source
public class WorkerReplying {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new AbstractVerticle() {
      @Override
      public void start() throws Exception {
        vertx.eventBus().consumer("the-address", msg -> {
          try {
            Thread.sleep(10);
            msg.reply("Executed by " + Thread.currentThread());
          } catch (InterruptedException e) {
            msg.fail(0, "Interrupted");
          }
        });
      }
    }, new DeploymentOptions().setWorker(true));
    for (int i = 0;i < 10;i++) {
      vertx.eventBus().send("the-address", "the-message", reply -> {
        System.out.println(reply.result().body());
      });
    }
  }
}
