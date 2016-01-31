package org.vietj.vertx.eventloop;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.docgen.Source;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@Source
public class PeriodicOnWorkerThread {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new AbstractVerticle() {
      @Override
      public void start() throws Exception {
        AtomicLong count = new AtomicLong(10);
        long now = System.currentTimeMillis();
        System.out.println("Starting periodic on " + Thread.currentThread());
        vertx.setPeriodic(1000, id -> {
          if (count.decrementAndGet() < 0) {
            vertx.cancelTimer(id);
            System.exit(0);
          }
          System.out.println("Periodic fired " + Thread.currentThread() + " after " + (System.currentTimeMillis() - now) + " ms");
        });
      }
    }, new DeploymentOptions().setWorker(true));
  }

  public static void source(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new AbstractVerticle() {
      @Override
      public void start() throws Exception {
        AtomicLong count = new AtomicLong(10);
        long now = System.currentTimeMillis();
        System.out.println("Starting periodic on " + Thread.currentThread());
        vertx.setPeriodic(1000, id -> {
          if (count.decrementAndGet() < 0) {
            vertx.cancelTimer(id);
          }
          System.out.println("Periodic fired " + Thread.currentThread() + " after " + (System.currentTimeMillis() - now) + " ms");
        });
      }
    }, new DeploymentOptions().setWorker(true));
  }
}
