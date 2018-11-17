package org.vietj.vertx.memcached;

public class MemcachedError extends RuntimeException {

  private final short status;

  public MemcachedError(short status) {
    this.status = status;
  }

  public short status() {
    return status;
  }
}
