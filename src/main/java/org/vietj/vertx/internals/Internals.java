package org.vietj.vertx.internals;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.internal.ContextInternal;
import io.vertx.core.internal.VertxInternal;
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

  public Future<Void> executeFromIO(ServerBootstrap bootstrap, SocketAddress socketAddress, ContextInternal context) {

    Handler<Channel> bindHandler = ch -> {
    };

    bootstrap.childHandler(new ChannelInitializer<Channel>() {
      @Override
      protected void initChannel(Channel ch) {
        context.emit(ch, bindHandler);
      }
    });

    Promise<Void> bindPromise = context.promise();

    bootstrap.bind(socketAddress).addListener(new ChannelFutureListener() {
      @Override
      public void operationComplete(ChannelFuture future) throws Exception {
        if (future.isSuccess()) {
          // Signal application with bind success
          bindPromise.complete();
        } else {
          // Signal application with bind error
          bindPromise.fail(future.cause());
        }
      }
    });

    return bindPromise.future();
  }
}
