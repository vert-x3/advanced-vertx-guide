package org.vietj.vertx.nettyserver.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.impl.ContextInternal;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {

  private ContextInternal context;
  private Handler<Future<Long>> requestHandler;

  public TimeServerHandler(ContextInternal context, Handler<Future<Long>> requestHandler) {
    this.context = context;
    this.requestHandler = requestHandler;
  }

  @Override
  public void channelActive(final ChannelHandlerContext ctx) {

    // Create a new blank future
    Future<Long> result = Future.future();

    // The handler is called when the user code complete the future
    result.setHandler(ar -> {

      // This block is pretty much borrowed from Netty's original example
      if (ar.succeeded()) {
        ByteBuf time = ctx.alloc().buffer(4);
        time.writeInt((int) (ar.result() / 1000L + 2208988800L));
        ChannelFuture f = ctx.writeAndFlush(time);
        f.addListener((ChannelFutureListener) channelFuture -> ctx.close());
      } else {
        ctx.close();
      }
    });

    // Dispatch to the request handler
    context.executeFromIO(() -> {
      requestHandler.handle(result);
    });
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    ctx.close();
  }
}
