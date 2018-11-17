package org.vietj.vertx.memcached;

import io.vertx.core.Vertx;

/**
 * You need to run a memcached instance, e.g with Docker
 *
 * docker run --rm --name my-memcache -p 11211:11211 -d memcached
 *
 */
public class MemcachedExample {

  public static void main(String[] args) {

    Vertx vertx = Vertx.vertx();

    // Create the memcached client
    MemcachedClient.connect(vertx, 11211, "localhost", ar1 -> {
      if (ar1.succeeded()) {

        // Connected to memcached
        System.out.println("connected");

        // Get the client
        MemcachedClient client = ar1.result();

        // Put a value
        client.set("foo", "bar", ar2 -> {
          if (ar2.succeeded()) {

            System.out.println("Put successful");

            // Now retrieve the same value
            client.get("foo", ar3 -> {
              if (ar3.succeeded()) {
                String res = ar3.result();
                System.out.println("Get successful " + res + "");
              } else {
                ar3.cause().printStackTrace();
              }
            });
          } else {
            ar2.cause().printStackTrace();
          }
        });
      } else {
        ar1.cause().printStackTrace();
      }
    });
  }
}
