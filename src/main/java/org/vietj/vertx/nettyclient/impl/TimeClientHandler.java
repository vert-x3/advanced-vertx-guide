package org.vietj.vertx.nettyclient.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.impl.ContextInternal;

public class TimeClientHandler extends ChannelInboundHandlerAdapter {

  private final ContextInternal context;
  private final Handler<AsyncResult<Long>> resultHandler;
  private final Future<Long> future = Future.future();

  public TimeClientHandler(ContextInternal context, Handler<AsyncResult<Long>> resultHandler) {
    this.context = context;
    this.resultHandler = resultHandler;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    ByteBuf m = (ByteBuf) msg;
    long currentTimeMillis;
    try {
      currentTimeMillis = (m.readUnsignedInt() - 2208988800L) * 1000L;
      if (!future.isComplete()) {
        future.complete(currentTimeMillis);

        // When we dispatch code to the Vert.x API we need to use executeFromIO
        context.executeFromIO(() -> {
          // Call the result handler when we get the result
          resultHandler.handle(future);
        });
      }
      ctx.close();
    } finally {
      m.release();
    }
  }
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    if (!future.isComplete()) {
      future.fail(cause);

      // When we dispatch code to the Vert.x API we need to use executeFromIO
      context.executeFromIO(() -> {
        resultHandler.handle(future);
      });
    }
    ctx.close();
  }
}
