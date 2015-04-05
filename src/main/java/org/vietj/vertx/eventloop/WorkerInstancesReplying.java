package org.vietj.vertx.eventloop;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.docgen.Source;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@Source
public class WorkerInstancesReplying {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(
        TheWorker.class.getName(),
        new DeploymentOptions().setWorker(true).setInstances(3)
    );
    for (int i = 0;i < 10;i++) {
      vertx.eventBus().send("the-address", "the-message", reply -> {
        System.out.println(reply.result().body());
      });
    }
  }
}
