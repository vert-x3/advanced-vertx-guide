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

  private int numberOfFiles;

  public void start() throws Exception {
    Context context = Vertx.currentContext();
    new Thread() {
      @Override
      public void run() {
        int n = getNumberOfFiles();
        context.runOnContext(v -> {
          // Runs on the same context
          numberOfFiles = n;
        });
      }
    };
  }

  private int getNumberOfFiles() {
    return 10; // Assuming this block
  }
}
