package org.vietj.vertx.memcached.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.memcache.binary.*;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.internal.VertxInternal;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.internal.net.NetSocketInternal;
import org.vietj.vertx.memcached.MemcachedClient;
import org.vietj.vertx.memcached.MemcachedError;

import java.nio.charset.StandardCharsets;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class MemcachedClientImpl implements MemcachedClient {

  public static Future<MemcachedClient> connect(Vertx vertx, int port, String host, NetClientOptions options) {

    // Create the NetClient
    NetClient tcpClient = options != null ? vertx.createNetClient(options) : vertx.createNetClient();

    // Connect to the memcached instance
    Future<NetSocket> connect = tcpClient.connect(port, host);
    return connect.map(so -> {
      // Create the client
      MemcachedClientImpl memcachedClient = new MemcachedClientImpl((VertxInternal) vertx, (NetSocketInternal) so);

      // Initialize the client: configure the pipeline and set the handlers
      memcachedClient.init();

      return memcachedClient;
    });
  }

  private final VertxInternal vertx;
  private final NetSocketInternal so;
  private final Deque<Promise<FullBinaryMemcacheResponse>> inflight = new ConcurrentLinkedDeque<>();

  private MemcachedClientImpl(VertxInternal vertx, NetSocketInternal so) {
    this.vertx = vertx;
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

  private Future<FullBinaryMemcacheResponse> writeRequest(BinaryMemcacheRequest request) {

    // Write the message, the memcached codec will encode the request to a buffer
    return so.writeMessage(request).compose(v -> {

      // The message has been encoded successfully and sent
      // Create a response promise and add it to the inflight queue, so it can be resolved by the server ack
      Promise<FullBinaryMemcacheResponse> promise = vertx.promise();
      inflight.add(promise);

      //
      return promise.future();
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
      Promise<FullBinaryMemcacheResponse> handler = inflight.poll();

      // Handle the message
      handler.complete(response);
    } finally {
      // Release the referenced counted message
      response.release();
    }
  }

  @Override
  public Future<@Nullable String> get(String key) {

    // Create the key buffer
    ByteBuf keyBuf = Unpooled.copiedBuffer(key, StandardCharsets.UTF_8);

    // Create the memcached request
    FullBinaryMemcacheRequest request = new DefaultFullBinaryMemcacheRequest(keyBuf, Unpooled.EMPTY_BUFFER);

    // Set the memcached operation opcode to perform a GET
    request.setOpcode(BinaryMemcacheOpcodes.GET);

    // Execute the request and process the response
    return writeRequest(request).map(response -> processGetResponse(response));
  }

  private String processGetResponse(FullBinaryMemcacheResponse response) {
    short status = response.status();
    switch (status) {

      case 0:
        // Succesfull get
        return response.content().toString(StandardCharsets.UTF_8);

      case 1:
        // Empty response -> null
        return null;

      default:
        // Memcached error
        throw new MemcachedError(status);
    }
  }

  @Override
  public Future<Void> set(String key, String value) {

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

    // Execute the request and process the response
    return writeRequest(request).map(response -> processSetResponse(response));
  }

  private Void processSetResponse(FullBinaryMemcacheResponse response) {
    short status = response.status();
    if (status == 0) {
      // Succesfull get
      return null;
    } else {
      // Memcached error
      throw new MemcachedError(status);
    }
  }
}
