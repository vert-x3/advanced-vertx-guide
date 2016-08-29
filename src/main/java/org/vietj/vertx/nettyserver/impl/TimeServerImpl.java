package org.vietj.vertx.nettyserver.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.impl.ContextInternal;
import io.vertx.core.impl.VertxInternal;
import org.vietj.vertx.nettyserver.TimeServer;

public class TimeServerImpl implements TimeServer {

  private final VertxInternal vertx;
  private Handler<Future<Long>> requestHandler;
  private ServerBootstrap bootstrap;
  private Channel channel;

  public TimeServerImpl(Vertx vertx) {
    this.vertx = (VertxInternal) vertx;
  }

  @Override
  public TimeServer requestHandler(Handler<Future<Long>> handler) {
    requestHandler = handler;
    return this;
  }

  @Override
  public void listen(int port, String host, Handler<AsyncResult<Void>> listenHandler) {
    if (requestHandler == null) {
      throw new IllegalStateException("No request handler set");
    }
    if (bootstrap != null) {
      throw new IllegalStateException("Already started");
    }

    // Get the current context as a Vert.x internal context
    ContextInternal context = vertx.getOrCreateContext();

    // The Vert.x internal context gives access to Netty's event loop
    // used as child group
    EventLoop eventLoop = context.nettyEventLoop();

    // The acceptor group is used as parent group
    EventLoopGroup acceptorGroup = vertx.getAcceptorEventLoopGroup();

    // Create and configure the Netty server bootstrap
    bootstrap = new ServerBootstrap();
    bootstrap.channel(NioServerSocketChannel.class);
    bootstrap.group(acceptorGroup, eventLoop);
    bootstrap.childHandler(new ChannelInitializer<Channel>() {
      @Override
      protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        TimeServerHandler handler = new TimeServerHandler(context, requestHandler);
        pipeline.addLast(handler);
      }
    });

    // Bind the server socket
    ChannelFuture bindFuture = bootstrap.bind(host, port);
    bindFuture.addListener(new ChannelFutureListener() {
      @Override
      public void operationComplete(ChannelFuture future) throws Exception {

        // When we dispatch code to the Vert.x API we need to use executeFromIO
        context.executeFromIO(() -> {

          // Callback the listen handler either with a success or a failure
          if (future.isSuccess()) {
            channel = future.channel();
            listenHandler.handle(Future.succeededFuture(null));
          } else {
            listenHandler.handle(Future.failedFuture(future.cause()));
          }
        });
      }
    });
  }

  @Override
  public void close() {
    if (channel != null) {
      channel.close();
      channel = null;
    }
  }
}
