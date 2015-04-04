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
public class ExecuteBlockingFailingFuture {

  public static void main(String[] args) {

    Vertx vertx = Vertx.vertx();
    vertx.runOnContext(v -> {

      Handler<Future<String>> blockingCodeHandler = future -> {
        try {
          throw new Exception();
        } catch (Exception e) {
          future.fail(e);
        }
      };

      Handler<AsyncResult<String>> resultHandler = result -> {
        if (result.succeeded()) {
          System.out.println("Got result");
        } else {
          System.out.println("Blocking code failed");
          result.cause().printStackTrace(System.out);
        }
      };

      vertx.executeBlocking(blockingCodeHandler, resultHandler);
    });
  }
}
