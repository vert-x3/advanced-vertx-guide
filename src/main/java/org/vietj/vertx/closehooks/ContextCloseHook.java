package org.vietj.vertx.closehooks;

import io.vertx.core.AsyncResult;
import io.vertx.core.Closeable;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.ContextInternal;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class ContextCloseHook implements Closeable {

  private final ContextInternal context;

  public ContextCloseHook(Vertx vertx) {
    this.context = (ContextInternal) vertx.getOrCreateContext();

    // Get notified when this context closes
    context.addCloseHook(this);
  }

  @Override
  public void close(Promise<Void> completion) {
    // Do cleanup, the method will complete the future
    doClose(completion);
  }

  /**
   * API close method - this is called by the user
   */
  public void close() {

    // Remove the hook
    context.removeCloseHook(this);

    // Do cleanup, the method will complete the future
    doClose(Promise.promise());
  }

  private void doClose(Promise<Void> fut) {

    // No-op, in reality it would be a resource like a Netty channel, a file, etc...
    fut.complete();
  }
}
