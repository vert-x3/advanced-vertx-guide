package org.vietj.vertx.nettyserver;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.vietj.vertx.nettyserver.impl.TimeServerImpl;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
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
  TimeServer requestHandler(Handler<Future<Long>> handler);

  /**
   * Start and bind the time server.
   *
   * @param port the server port
   * @param host the server host
   * @param listenHandler the listen result handler
   */
  void listen(int port, String host, Handler<AsyncResult<Void>> listenHandler);

  /**
   * Close the time server.
   */
  void close();

}
