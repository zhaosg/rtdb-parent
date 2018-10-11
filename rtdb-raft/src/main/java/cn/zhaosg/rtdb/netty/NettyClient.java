package cn.zhaosg.rtdb.netty;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Promise;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;

public class NettyClient {
    private EventLoopGroup group;
    private String host;
    private int port;

    public void init(String host, int port) {
        this.host = host;
        this.port = port;

    }

    public void send(Object request) {
        group = new NioEventLoopGroup(5, new ThreadFactoryBuilder()
                .setNameFormat("drift-client-%s")
                .setDaemon(true)
                .build());
        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class).
                option(ChannelOption.TCP_NODELAY, true).
                option(CONNECT_TIMEOUT_MILLIS, 300).
                handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        ch.pipeline().addLast("decoder", new KryoDecoder());
                        ch.pipeline().addLast("encoder", new KryoEncoder());
                        p.addLast(new EchoClientHandler());
                    }
                });
        Promise<Channel> promise = group.next().newPromise();
        b.connect(host, port).addListener((ChannelFutureListener) future -> notifyConnect(future, promise));
        promise.addListener(channelFuture -> {
            try {
                if (channelFuture.isSuccess()) {
                    Channel cc = (Channel) channelFuture.getNow();
                    ChannelFuture sendFuture = cc.writeAndFlush(request);
                    sendFuture.addListener(cf -> {
                        try {
                            if (!cf.isSuccess()) {
//                    fatalError(channelFuture.cause());
                            } else {
                                System.out.println("ok");
                            }
                        } catch (Throwable t) {
//                fatalError(t);
                        }
                    });
                } else {
//                        fatalError(new ConnectionFailedException(request.getAddress(), channelFuture.cause()));
                }
            } catch (Throwable t) {
//                    fatalError(t);
            }
        });
    }

    private static void notifyConnect(ChannelFuture future, Promise<Channel> promise) {
        if (future.isSuccess()) {
            Channel channel = future.channel();
            if (!promise.trySuccess(channel)) {
                // Promise was completed in the meantime (likely cancelled), just release the channel again
                channel.close();
            }
        } else {
            promise.tryFailure(future.cause());
        }
    }
}
