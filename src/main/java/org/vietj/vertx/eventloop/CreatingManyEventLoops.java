package org.vietj.vertx.eventloop;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.docgen.Source;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@Source
public class CreatingManyEventLoops {

  public static void main(String[] args) throws Exception {
    System.out.println(Thread.currentThread());
    Vertx vertx = Vertx.vertx();
    for (int i = 0; i < 20; i++) {
      int index = i;
      CountDownLatch latch = new CountDownLatch(1);
      vertx.setTimer(1, id -> {
        System.out.println(index + ":" + Thread.currentThread());
        latch.countDown();
      });
      latch.await(2, TimeUnit.SECONDS);
    }
    System.exit(0);
  }

  public static void source() {
    System.out.println(Thread.currentThread());
    Vertx vertx = Vertx.vertx();
    for (int i = 0; i < 20; i++) {
      int index = i;
      vertx.setTimer(1, timerID -> {
        System.out.println(index + ":" + Thread.currentThread());
      });
    }
  }
}
