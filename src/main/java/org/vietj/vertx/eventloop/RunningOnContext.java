package org.vietj.vertx.eventloop;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.docgen.Source;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@Source
public class RunningOnContext extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new RunningOnContext());
  }

  private int numberOfFiles;

  public void start() throws Exception {
    Context context = Vertx.currentContext();

    System.out.println("Running with context : " + Vertx.currentContext());

    // Our blocking action
    Thread thread = new Thread() {
      public void run() {

        // No context here!
        System.out.println("Current context : " + Vertx.currentContext());

        int n = getNumberOfFiles();
        context.runOnContext(v -> {

          // Runs on the same context
          System.out.println("Runs on the original context : " + Vertx.currentContext());
          numberOfFiles = n;
        });
      }
    };

    //
    thread.start();
  }

  private int getNumberOfFiles() {
    return 10; // Assuming this block
  }
}
