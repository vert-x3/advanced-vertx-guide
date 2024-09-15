package org.vietj.vertx.nettyclient.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.internal.ContextInternal;
import org.vietj.vertx.nettyclient.TimeClient;

public class TimeClientImpl implements TimeClient {

  private final Vertx vertx;

  public TimeClientImpl(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public Future<Long> getTime(int port, String host) {
    // Get the current context as a Vert.x internal context
    ContextInternal context = (ContextInternal) vertx.getOrCreateContext();

    Promise<Long> promise = context.promise();

    Bootstrap bootstrap = createBootstrap(context, promise);

    // Connect to the server
    connect(bootstrap, port, host, promise);

    return promise.future();
  }

  private Bootstrap createBootstrap(ContextInternal context, Promise<Long> result) {
    // The Vert.x internal context gives access to Netty's event loop
    EventLoop eventLoop = context.nettyEventLoop();  // <1>

    // Create and configure the Netty bootstrap
    Bootstrap bootstrap = new Bootstrap(); // <2>
    bootstrap.group(eventLoop);
    bootstrap.channel(NioSocketChannel.class);
    bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
    bootstrap.handler(new ChannelInitializer<Channel>() {
      @Override
      protected void initChannel(Channel ch) {
        ChannelPipeline pipeline = ch.pipeline(); // <3>
        pipeline.addLast(new TimeClientHandler(result));
      }
    });

    return bootstrap;
  }

  private void connect(Bootstrap bootstrap,
                       int port,
                       String host,
                       Promise<Long> result) {
    ChannelFuture connectFuture = bootstrap.connect(host, port); // <1>
    connectFuture.addListener(new ChannelFutureListener() {
      @Override
      public void operationComplete(ChannelFuture future) throws Exception {
        if (!future.isSuccess()) {
          result.fail(future.cause()); // 2
        }
      }
    });
  }
}
