package org.vietj.vertx.closehooks;

import io.vertx.core.AsyncResult;
import io.vertx.core.Closeable;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class ContextCloseHook implements Closeable {

  private final Context context;

  public ContextCloseHook(Vertx vertx) {
    this.context = vertx.getOrCreateContext();

    // Get notified when this context closes
    context.addCloseHook(this);
  }

  @Override
  public void close(Handler<AsyncResult<Void>> completionHandler) {
    // Create a new future
    Future<Void> fut = Future.future();

    // Set the close handler to be notified when the future resolves
    fut.setHandler(completionHandler);

    // Do cleanup, the method will complete the future
    doClose(fut);
  }

  /**
   * API close method - this is called by the user
   */
  public void close() {

    // Remove the hook
    context.removeCloseHook(this);

    // Do cleanup, the method will complete the future
    doClose(Future.future());
  }

  private void doClose(Future<Void> fut) {

    // No-op, in reality it would be a resource like a Netty channel, a file, etc...
    fut.complete();
  }
}
