package org.vietj.vertx.nettyclient;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.vietj.vertx.nettyclient.impl.TimeClientImpl;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@VertxGen
public interface TimeClient {

  static TimeClient create(Vertx vertx) {
    return new TimeClientImpl(vertx);
  }

  void getTime(int port, String host, Handler<AsyncResult<Long>> resultHandler);

}
