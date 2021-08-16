package org.vietj.vertx.nettyserver.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.impl.ContextInternal;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {

  private ContextInternal context;
  private Handler<Promise<Long>> requestHandler;

  public TimeServerHandler(ContextInternal context, Handler<Promise<Long>> requestHandler) {
    this.context = context;
    this.requestHandler = requestHandler;
  }

  @Override
  public void channelActive(final ChannelHandlerContext ctx) {

    Promise<Long> result = Promise.promise(); // <1>

    context.emit(result, requestHandler); // <2>

    result.future().onComplete(ar -> { // <3>
      if (ar.succeeded()) {  // <4>
        ByteBuf time = ctx.alloc().buffer(4);
        time.writeInt((int) (ar.result() / 1000L + 2208988800L));
        ChannelFuture f = ctx.writeAndFlush(time);
        f.addListener((ChannelFutureListener) channelFuture -> ctx.close());
      } else {  // <5>
        ctx.close();
      }
    });
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    ctx.close();
  }
}
