package org.vietj.vertx.internals;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.impl.ContextInternal;
import io.vertx.core.impl.VertxInternal;
import io.vertx.docgen.Source;

import java.net.SocketAddress;

@Source
public class Internals {

  public void exampleGetOrCreateContext(Vertx vertx) {

    Context context = vertx.getOrCreateContext();

    // Cast to access extra methods
    Internals contextInternal = (Internals) context;
  }

  public void exampleBoostrap(Context context) {

    ContextInternal contextInt = (ContextInternal) context; // <1>
    EventLoop eventLoop = contextInt.nettyEventLoop();

    Bootstrap bootstrap = new Bootstrap(); // <2>
    bootstrap.channel(NioSocketChannel.class);
    bootstrap.group(eventLoop);
  }

  public void exampleServerBootstrap(Context context) {

    ContextInternal contextInt = (ContextInternal) context; // <1>
    EventLoop eventLoop = contextInt.nettyEventLoop();

    VertxInternal vertxInt = contextInt.owner(); // <2>
    EventLoopGroup acceptorGroup = vertxInt.getAcceptorEventLoopGroup();

    ServerBootstrap bootstrap = new ServerBootstrap(); // <3>
    bootstrap.channel(NioServerSocketChannel.class);
    bootstrap.group(acceptorGroup, eventLoop);
  }

  public void executeFromIO(ServerBootstrap bootstrap, SocketAddress socketAddress, ContextInternal context) {

    Handler<ChannelFuture> bindHandler = fut -> {
      if (fut.isSuccess()) {
        // Signal application with bind success
      } else {
        // Signal application with bind error
      }
    };

    // <1>
    bootstrap.bind(socketAddress).addListener(new ChannelFutureListener() {
      @Override
      public void operationComplete(ChannelFuture future) throws Exception {
        // <2>
        context.dispatch(future, bindHandler);
      }
    });
  }
}
