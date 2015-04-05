package org.vietj.vertx.eventloop;

import io.vertx.core.AbstractVerticle;
import io.vertx.docgen.Source;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@Source
public class TheWorker extends AbstractVerticle {

  static final AtomicInteger serial = new AtomicInteger();

  final int id = serial.incrementAndGet();

  @Override
  public void start() throws Exception {
    vertx.eventBus().consumer("the-address", msg -> {
      try {
        Thread.sleep(10);
        msg.reply("Executed by worker " + id + " with " + Thread.currentThread());
      } catch (InterruptedException e) {
        msg.fail(0, "Interrupted");
      }
    });
  }
}
