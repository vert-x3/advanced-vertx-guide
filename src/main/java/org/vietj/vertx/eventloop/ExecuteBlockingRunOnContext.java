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
public class ExecuteBlockingRunOnContext {

  public static void main(String[] args) throws Exception {
    new ExecuteBlockingRunOnContext().execute(Vertx.vertx());
  }

  public void execute(Vertx vertx) {
    vertx.runOnContext(v -> {

      // On the event loop
      System.out.println("Calling blocking block from " + Thread.currentThread());

      Handler<Future<String>> blockingCodeHandler = future -> {
        // Non event loop
        System.out.println("Computing with " + Thread.currentThread());

        // Running on context from the worker
        vertx.runOnContext(v2 -> {
          System.out.println("Running on context from the worker " + Thread.currentThread());
        });
      };

      // Execute the blocking code handler and the associated result handler
      vertx.executeBlocking(blockingCodeHandler, result -> {});
    });
  }
}
