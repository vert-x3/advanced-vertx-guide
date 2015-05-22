package org.vietj.vertx.eventloop;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class ConfigureThreadPool {

  public static void eventLoop() {
    Vertx vertx = Vertx.vertx(new VertxOptions().setEventLoopPoolSize(10));
  }

  public static void worker() {
    Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(10));
  }
}
