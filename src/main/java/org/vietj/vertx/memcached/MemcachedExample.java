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

    MemcachedClient.connect(vertx, 11211, "localhost")
        .compose(client -> {
          System.out.println("connected");

          // Put a value
          return client.set("foo", "bar").compose(v -> {
            System.out.println("Put successful");

            // Now retrieve the same value
            return client.get("foo");
          });
        }).onSuccess(res -> {
          System.out.println("Get successful " + res + "");
        }).onFailure(err -> err.printStackTrace());
  }
}
