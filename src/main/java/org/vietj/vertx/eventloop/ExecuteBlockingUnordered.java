package org.vietj.vertx.eventloop;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.docgen.Source;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@Source
public class ExecuteBlockingUnordered {

  public static void main(String[] args) throws Exception {
    new ExecuteBlockingUnordered().execute(Vertx.vertx());
  }

  Handler<Future<String>> blockingCodeHandler1 = future -> {};
  Handler<Future<String>> blockingCodeHandler2 = future -> {};
  Handler<Future<String>> blockingCodeHandler3 = future -> {};

  public void execute(Vertx vertx) {
    vertx.runOnContext(v -> {

      // The blocks are executed on any available worker thread
      vertx.executeBlocking(blockingCodeHandler1, false, ar -> {});
      vertx.executeBlocking(blockingCodeHandler2, false, ar -> {});
      vertx.executeBlocking(blockingCodeHandler3, false, ar -> {});
    });
  }
}
