package org.vietj.vertx.nettyclient.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.vertx.core.AsyncResult;
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
    ContextInternal context = (ContextInternal) vertx.getOrCreateContext();
    Bootstrap bootstrap = new Bootstrap();
    bootstrap.group(context.nettyEventLoop());
    bootstrap.channel(NioSocketChannel.class);
    bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
    bootstrap.handler(new ChannelInitializer<Channel>() {
      @Override
      protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new TimeClientHandler(context, resultHandler));
      }
    });
    ChannelFuture channelFuture = bootstrap.connect(host, port);
    channelFuture.addListener(new ChannelFutureListener() {
      @Override
      public void operationComplete(ChannelFuture future) throws Exception {
        if (!future.isSuccess()) {
          resultHandler.handle(io.vertx.core.Future.failedFuture(future.cause()));
        }
      }
    });
  }
}
