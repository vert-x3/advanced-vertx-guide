package org.vietj.vertx.eventloop;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.docgen.Source;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@Source
public class TimerOnWorkerThread {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new AbstractVerticle() {
      @Override
      public void start() throws Exception {
        long now = System.currentTimeMillis();
        System.out.println("Starting timer on " + Thread.currentThread());
        vertx.setTimer(1000, id -> {
          System.out.println("Timer fired " + Thread.currentThread() + " after " + (System.currentTimeMillis() - now) + " ms");
          System.exit(0);
        });
      }
    }, new DeploymentOptions().setWorker(true));
  }

  public static void source() {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new AbstractVerticle() {
      @Override
      public void start() throws Exception {
        long now = System.currentTimeMillis();
        System.out.println("Starting timer on " + Thread.currentThread());
        vertx.setTimer(1000, id -> {
          System.out.println("Timer fired " + Thread.currentThread() + " after " + (System.currentTimeMillis() - now) + " ms");
        });
      }
    }, new DeploymentOptions().setWorker(true));
  }
}
