package org.vietj.vertx.nettyclient;

import io.vertx.core.Vertx;

import java.util.Date;

public class TimeClientExample {

  public static void main(String[] args) {

    Vertx vertx = Vertx.vertx();

    // Create the time client
    TimeClient server = TimeClient.create(vertx);

    // Fetch the time
    server.getTime(8037, "localhost").onComplete(ar -> {
      if (ar.succeeded()) {
        System.out.println("Time is " + new Date(ar.result()));
      } else {
        ar.cause().printStackTrace();
      }
    });
  }
}
