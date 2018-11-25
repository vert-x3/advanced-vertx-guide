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
  private Handler<AsyncResult<Long>> resultHandler;

  public TimeClientHandler(ContextInternal context, Handler<AsyncResult<Long>> resultHandler) {
    this.context = context;
    this.resultHandler = resultHandler;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    ByteBuf m = (ByteBuf) msg;
    long currentTimeMillis;
    try {
      currentTimeMillis = (m.readUnsignedInt() - 2208988800L) * 1000L; // <1>
      context.executeFromIO(Future.succeededFuture(currentTimeMillis), resultHandler); // <2>
      resultHandler = null; // <3>
      ctx.close(); // <4>
    } finally {
      m.release();
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    if (resultHandler != null) {

      // When we dispatch code to the Vert.x API we need to use executeFromIO
      context.executeFromIO(Future.failedFuture(cause), resultHandler);

      // Set the handler to null
      resultHandler = null;
    }
    ctx.close();
  }
}
