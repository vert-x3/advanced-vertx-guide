package org.vietj.vertx.eventloop;

import io.vertx.core.Vertx;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public abstract class TaskBase {

  private final long timeout;

  public TaskBase(long timeout) {
    this.timeout = timeout;
  }

  public TaskBase() {
    this(500);
  }

  public void run() throws Exception {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    PrintStream prev = System.out;
    System.setOut(new PrintStream(buffer));
    long now = System.currentTimeMillis();
    Vertx vertx = Vertx.vertx();
    try {
      execute(vertx);
      long wait = timeout - (System.currentTimeMillis() - now);
      if (wait > 0) {
        Thread.sleep(wait);
      }
    } finally {
      System.setOut(prev);
      vertx.close();
    }
    try (FileOutputStream log = new FileOutputStream(
        "src/main/asciidoc/" + getClass().getName() + ".txt")) {
      log.write(buffer.toByteArray());
    }
  }

  public abstract void execute(Vertx vertx);

}
