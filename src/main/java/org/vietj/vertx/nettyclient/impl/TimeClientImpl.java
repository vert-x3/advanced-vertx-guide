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
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.impl.ContextInternal;
import org.vietj.vertx.nettyclient.TimeClient;

public class TimeClientImpl implements TimeClient {

  private final Vertx vertx;

  public TimeClientImpl(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public void getTime(int port, String host, Handler<AsyncResult<Long>> resultHandler) {

    // Get the current context as a Vert.x internal context
    ContextInternal context = (ContextInternal) vertx.getOrCreateContext();

    // The Vert.x internal context gives access to Netty's event loop
    EventLoop eventLoop = context.nettyEventLoop();

    // Create and configure the Netty bootstrap
    Bootstrap bootstrap = new Bootstrap();
    bootstrap.group(eventLoop);
    bootstrap.channel(NioSocketChannel.class);
    bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
    bootstrap.handler(new ChannelInitializer<Channel>() {
      @Override
      protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new TimeClientHandler(context, resultHandler));
      }
    });

    // Connect to the server
    ChannelFuture channelFuture = bootstrap.connect(host, port);
    channelFuture.addListener(new ChannelFutureListener() {
      @Override
      public void operationComplete(ChannelFuture future) throws Exception {
        if (!future.isSuccess()) {

          // When we dispatch code to the Vert.x API we need to use executeFromIO
          context.executeFromIO(v -> {
            // Upon connect error we call the result handler with a failure
            resultHandler.handle(io.vertx.core.Future.failedFuture(future.cause()));
          });
        }
      }
    });
  }
}
