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
public class ExecuteBlockingSuccess {

  public static void main(String[] args) {

    Vertx vertx = Vertx.vertx();
    vertx.runOnContext(v -> {

      // On the event loop
      System.out.println("Calling blocking block from " + Thread.currentThread());

      Handler<Future<String>> blockingCodeHandler = future -> {
        // Non event loop
        System.out.println("Computing with " + Thread.currentThread());
        future.complete("some result");
      };

      Handler<AsyncResult<String>> resultHandler = result -> {
        // Back to the event loop
        System.out.println("Got result in " + Thread.currentThread());
      };

      // Execute the blocking code handler and the associated result handler
      vertx.executeBlocking(blockingCodeHandler, resultHandler);
    });
  }
}
