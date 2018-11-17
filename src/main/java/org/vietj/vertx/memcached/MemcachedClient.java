package org.vietj.vertx.memcached;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetClientOptions;
import org.vietj.vertx.memcached.impl.MemcachedClientImpl;

/**
 * A simple Memcached client.
 */
public interface MemcachedClient {

  /**
   * Connect to memcached, the {@code completionHandler} will get the {@link MemcachedClient} instance.
   */
  static void connect(Vertx vertx, int port, String host, Handler<AsyncResult<MemcachedClient>> completionHandler) {
    MemcachedClientImpl.connect(vertx, port, host, null, completionHandler);
  }

  /**
   * Connect to memcached, the {@code completionHandler} will get the {@link MemcachedClient} instance.
   */
  static void connect(Vertx vertx, int port, String host, NetClientOptions options, Handler<AsyncResult<MemcachedClient>> completionHandler) {
    MemcachedClientImpl.connect(vertx, port, host, options, completionHandler);
  }

  /**
   * Get a cached entry.
   *
   * @param key the entry key
   * @param completionHandler the handler called with the result
   */
  void get(String key, Handler<AsyncResult<@Nullable String>> completionHandler);

  /**
   * Set a cached entry.
   *
   * @param key the entry key
   * @param value the entry value
   * @param completionHandler the handler called with the result
   */
  void set(String key, String value, Handler<AsyncResult<Void>> completionHandler);

}
