package cn.zhaosg.rtdb.base;

import cn.zhaosg.rtdb.raft.AppendLogResponse;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;

public class Client {
    private Logger logger = LoggerFactory.getLogger(Client.class);
    private EventLoopGroup group;
    private String host;
    private int port;

    public Client() {
    }

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void init(String host, int port) {
        this.host = host;
        this.port = port;
        if (group == null)
            group = new NioEventLoopGroup(5, new ThreadFactoryBuilder()
                    .setNameFormat("drift-client-%s")
                    .setDaemon(true)
                    .build());
    }

    public <T> void send(Object request, Consumer<T> dataReady) {
        Bootstrap b = new Bootstrap();
        final RaftClientHandler hander = new RaftClientHandler((object) -> {
            if (object instanceof AppendLogResponse) {
                dataReady.accept((T) object);
            }
        });
        b.group(group).channel(NioSocketChannel.class).
                option(ChannelOption.TCP_NODELAY, true).
                option(CONNECT_TIMEOUT_MILLIS, 5000).
                handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast("decoder", new Coders.Decoder());
                        p.addLast("encoderRequest", new Coders.AppendLogRequestEncoder());
                        p.addLast("encoderResponse", new Coders.AppendLogResponseEncoder());
                        p.addLast(hander);
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
                                logger.error("", cf.cause());
                            }
                        } catch (Throwable t) {
                            logger.error("", t);
                        }
                    });
                } else {
                    logger.error("", channelFuture.cause());
                }
            } catch (Throwable t) {
                logger.error("", t);
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

    public class RaftClientHandler extends ChannelInboundHandlerAdapter {
        private Consumer<Object> dataReady;

        public RaftClientHandler() {

        }

        public RaftClientHandler(Consumer<Object> dataReady) {
            this.dataReady = dataReady;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            dataReady.accept(msg);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
            ctx.close();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
