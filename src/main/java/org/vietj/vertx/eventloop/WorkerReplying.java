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
            System.out.println("Executed by " + Thread.currentThread());
            msg.reply("whatever");
          } catch (InterruptedException e) {
            msg.fail(0, "Interrupted");
          }
        });
      }
    }, new DeploymentOptions().setWorker(true));

    // Send 10 messages
    send(vertx, 10);
  }

  static void send(Vertx vertx, int count) {
    // We send when we get the reply in order to not send all messages at the same time
    // otherwise they might be using the same worker thread and that would defeat the purpose
    // of this example
    if (count >= 0) {
      vertx.eventBus().send("the-address", count, reply -> {
        send(vertx, count - 1);
      });
    }
  }
}
