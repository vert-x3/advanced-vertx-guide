package org.vietj.vertx.eventloop;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.docgen.Source;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@Source
public class WorkerInstancesReplyingLowThreads {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(2));
    vertx.deployVerticle(
        TheWorker.class.getName(),
        new DeploymentOptions().setWorker(true).setInstances(4)
    );
    for (int i = 0;i < 10;i++) {
      vertx.eventBus().send("the-address", "the-message", reply -> {
        System.out.println(reply.result().body());
      });
    }
  }
}
