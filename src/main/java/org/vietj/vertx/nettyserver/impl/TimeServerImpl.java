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
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.ContextInternal;
import io.vertx.core.impl.VertxInternal;
import org.vietj.vertx.nettyserver.TimeServer;

public class TimeServerImpl implements TimeServer {

  private final VertxInternal vertx;
  private Handler<Promise<Long>> requestHandler;
  private ServerBootstrap bootstrap;
  private Channel channel;
  private ContextInternal context;

  public TimeServerImpl(Vertx vertx) {
    this.vertx = (VertxInternal) vertx;
  }

  @Override
  public TimeServer requestHandler(Handler<Promise<Long>> handler) {
    requestHandler = handler;
    return this;
  }

  @Override
  public Future<Void> listen(int port, String host) {
    ContextInternal ctx = vertx.getOrCreateContext();

    if (requestHandler == null) {
      return ctx.failedFuture(new IllegalStateException("No request handler set"));
    }
    if (bootstrap != null) {
      return ctx.failedFuture(new IllegalStateException("Already started"));
    }

    // Get the current context as a Vert.x internal context
    context = ctx;
    createBootstrap();

    // Bind the server socket
    return bind(host, port);
  }

  private void createBootstrap() {
    EventLoopGroup acceptorGroup = vertx.getAcceptorEventLoopGroup(); // <1>
    EventLoop eventLoop = context.nettyEventLoop(); // <2>
    bootstrap = new ServerBootstrap(); // <3>
    bootstrap.channel(NioServerSocketChannel.class);
    bootstrap.group(acceptorGroup, eventLoop);
    bootstrap.childHandler(new ChannelInitializer<Channel>() {
      @Override
      protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline(); // <4>
        TimeServerHandler handler = new TimeServerHandler(context, requestHandler);
        pipeline.addLast(handler);
      }
    });
  }

  private Future<Void> bind(String host, int port) {

    Promise<Void> promise = context.promise(); // <1>

    ChannelFuture bindFuture = bootstrap.bind(host, port);
    bindFuture.addListener(new ChannelFutureListener() {
      @Override
      public void operationComplete(ChannelFuture future) {
        // <2>
        if (future.isSuccess()) {
          channel = future.channel();
          promise.complete();
        } else {
          promise.fail(future.cause());
        }
      }
    });

    return promise.future(); // <3>
  }

  @Override
  public void close() {
    if (channel != null) {
      channel.close();
      channel = null;
    }
  }
}
