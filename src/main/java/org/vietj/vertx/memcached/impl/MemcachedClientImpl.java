package org.vietj.vertx.memcached.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.memcache.binary.*;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.impl.NetSocketInternal;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import org.vietj.vertx.memcached.MemcachedClient;
import org.vietj.vertx.memcached.MemcachedError;

import java.nio.charset.StandardCharsets;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class MemcachedClientImpl implements MemcachedClient {

  public static void connect(Vertx vertx, int port, String host, NetClientOptions options, Handler<AsyncResult<MemcachedClient>> completionHandler) {

    // Create the NetClient
    NetClient client = options != null ? vertx.createNetClient(options) : vertx.createNetClient();

    // Connect to the memcached instance
    client.connect(port, host, ar -> {
      if (ar.succeeded()) {
        // Get the socket
        NetSocketInternal so = (NetSocketInternal) ar.result();

        // Create the client
        MemcachedClientImpl memcachedClient = new MemcachedClientImpl(so);

        // Initialize the client: configure the pipeline and set the handlers
        memcachedClient.init();

        // Return the memcached instance to the client
        completionHandler.handle(Future.succeededFuture(memcachedClient));
      } else {
        completionHandler.handle(Future.failedFuture(ar.cause()));
      }
    });
  }

  private final NetSocketInternal so;
  private final Deque<Handler<AsyncResult<FullBinaryMemcacheResponse>>> inflight = new ConcurrentLinkedDeque<>();

  private MemcachedClientImpl(NetSocketInternal so) {
    this.so = so;
  }

  /**
   * Initialize the client, this will configure Netty's pipeline an set an handler to process the
   * decoded messages.
   */
  private void init() {

    // Get the pipeline
    ChannelPipeline pipeline = so.channelHandlerContext().pipeline();

    // Add the memcached message aggregator
    pipeline.addFirst("aggregator", new BinaryMemcacheObjectAggregator(Integer.MAX_VALUE));

    // Add the memcached decoder
    pipeline.addFirst("memcached", new BinaryMemcacheClientCodec());

    // Set the message handler to process memcached message
    so.messageHandler(this::processResponse);
  }

  private void writeRequest(BinaryMemcacheRequest request, Handler<AsyncResult<FullBinaryMemcacheResponse>> completionHandler) {

    // Write the message, the memcached codec will encode the request
    // to a buffer and it will be sent
    so.writeMessage(request, ar -> {
      if (ar.succeeded()) {
        // The message has been encoded succesfully and sent
        // we add the handler to the inflight queue
        inflight.add(completionHandler);
      } else {
        // The message could not be encoded or sent
        // we signal an error
        completionHandler.handle(Future.failedFuture(ar.cause()));
      }
    });
  }

  /**
   * Process the memcached response
   *
   * @param msg the response message
   */
  private void processResponse(Object msg) {
    // That must be a memcached message
    FullBinaryMemcacheResponse response = (FullBinaryMemcacheResponse) msg;

    try {
      // Get the handler that will process the response
      Handler<AsyncResult<FullBinaryMemcacheResponse>> handler = inflight.poll();

      // Handle the message
      handler.handle(Future.succeededFuture(response));
    } finally {
      // Release the referenced counted message
      response.release();
    }
  }

  @Override
  public void get(String key, Handler<AsyncResult<@Nullable String>> completionHandler) {

    // Create the key buffer
    ByteBuf keyBuf = Unpooled.copiedBuffer(key, StandardCharsets.UTF_8);

    // Create the memcached request
    FullBinaryMemcacheRequest request = new DefaultFullBinaryMemcacheRequest(keyBuf, Unpooled.EMPTY_BUFFER);

    // Set the memcached operation opcode to perform a GET
    request.setOpcode(BinaryMemcacheOpcodes.GET);

    // Execute the request
    writeRequest(request, ar -> {
      if (ar.succeeded()) {
        // Get the response
        processGetResponse(ar.result(), completionHandler);
      } else {
        // Network error
        completionHandler.handle(Future.failedFuture(ar.cause()));
      }
    });
  }

  private void processGetResponse(FullBinaryMemcacheResponse response, Handler<AsyncResult<@Nullable String>> completionHandler) {
    short status = response.status();
    switch (status) {

      case 0:
        // Succesfull get
        String value = response.content().toString(StandardCharsets.UTF_8);
        completionHandler.handle(Future.succeededFuture(value));
        break;

      case 1:
        // Empty response -> null
        completionHandler.handle(Future.succeededFuture());
        break;

      default:
        // Memcached error
        completionHandler.handle(Future.failedFuture(new MemcachedError(status)));
        break;
    }
  }

  @Override
  public void set(String key, String value, Handler<AsyncResult<Void>> completionHandler) {

    // Create the key buffer
    ByteBuf keyBuf = Unpooled.copiedBuffer(key, StandardCharsets.UTF_8);

    // Create the value buffer
    ByteBuf valueBuf = Unpooled.copiedBuffer(value, StandardCharsets.UTF_8);

    // Extra : deadbeef + 2 hours expiration time
    ByteBuf extra = Unpooled.copyLong(0xdeadbeef00001c20L);

    // Create the memcached request
    FullBinaryMemcacheRequest request = new DefaultFullBinaryMemcacheRequest(keyBuf, extra, valueBuf);

    // Set the memcached operation opcode to perform a SET
    request.setOpcode(BinaryMemcacheOpcodes.SET);

    // Execute the request
    writeRequest(request, ar -> {
      if (ar.succeeded()) {
        processSetResponse(ar.result(), completionHandler);
      } else {
        // Network error
        completionHandler.handle(Future.failedFuture(ar.cause()));
      }
    });
  }

  private void processSetResponse(FullBinaryMemcacheResponse response, Handler<AsyncResult<Void>> completionHandler) {
    short status = response.status();
    if (status == 0) {
      // Succesfull get
      completionHandler.handle(Future.succeededFuture());
    } else {
      // Memcached error
      completionHandler.handle(Future.failedFuture(new MemcachedError(status)));
    }
  }
}
