package org.vietj.vertx.nettyclient.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.vertx.core.Promise;

public class TimeClientHandler extends ChannelInboundHandlerAdapter {

  private Promise<Long> resultPromise;

  public TimeClientHandler(Promise<Long> resultPromise) {
    this.resultPromise = resultPromise;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    ByteBuf m = (ByteBuf) msg;
    long currentTimeMillis;
    try {
      currentTimeMillis = (m.readUnsignedInt() - 2208988800L) * 1000L; // <1>
      resultPromise.complete(currentTimeMillis);  // <2>
      resultPromise = null; // <3>
      ctx.close(); // <4>
    } finally {
      m.release();
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    if (resultPromise != null) {

      // TODO
      resultPromise.fail(cause);

      // Set the handler to null
      resultPromise = null;
    }
    ctx.close();
  }
}
