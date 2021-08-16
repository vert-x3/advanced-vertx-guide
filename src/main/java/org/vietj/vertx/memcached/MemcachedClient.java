package org.vietj.vertx.memcached;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
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
  static Future<MemcachedClient> connect(Vertx vertx, int port, String host) {
    return MemcachedClientImpl.connect(vertx, port, host, null);
  }

  /**
   * Connect to memcached, the {@code completionHandler} will get the {@link MemcachedClient} instance.
   */
  static Future<MemcachedClient> connect(Vertx vertx, int port, String host, NetClientOptions options) {
    return MemcachedClientImpl.connect(vertx, port, host, options);
  }

  /**
   * Get a cached entry.
   *
   * @param key the entry key
   * @param completionHandler the handler called with the result
   */
  Future<@Nullable String> get(String key);

  /**
   * Set a cached entry.
   *
   * @param key the entry key
   * @param value the entry value
   * @param completionHandler the handler called with the result
   */
  Future<Void> set(String key, String value);

}
