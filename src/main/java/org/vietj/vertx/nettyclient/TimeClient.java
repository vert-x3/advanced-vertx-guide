package org.vietj.vertx.nettyclient;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.vietj.vertx.nettyclient.impl.TimeClientImpl;

public interface TimeClient {

  /**
   * @return a new time client
   */
  static TimeClient create(Vertx vertx) {
    return new TimeClientImpl(vertx);
  }

  /**
   * Fetch the current time from a server.
   *
   * @param port the server port
   * @param host the server host name
   * @param resultHandler the asynchronous time result
   */
  void getTime(int port, String host, Handler<AsyncResult<Long>> resultHandler);

}
