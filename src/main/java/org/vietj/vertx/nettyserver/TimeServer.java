package org.vietj.vertx.nettyserver;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.vietj.vertx.nettyserver.impl.TimeServerImpl;

public interface TimeServer {

  /**
   * @return a new time server
   */
  static TimeServer create(Vertx vertx) {
    return new TimeServerImpl(vertx);
  }

  /**
   * Set the handler to be called when a time request happens. The handler should complete
   * the future with the time value.
   *
   * @param handler the handler to be called
   * @return this object
   */
  TimeServer requestHandler(Handler<Promise<Long>> handler);

  /**
   * Start and bind the time server.
   *
   * @param port the server port
   * @param host the server host
   * @return the future completed when the socket is bound
   */
  Future<Void> listen(int port, String host);

  /**
   * Close the time server.
   */
  void close();

}
