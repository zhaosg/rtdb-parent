package cn.zhaosg.rtdb.raft;

import cn.zhaosg.rtdb.netty.KryoDecoder;
import cn.zhaosg.rtdb.netty.KryoEncoder;
import cn.zhaosg.rtdb.netty.NettyClient;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class RaftServer {

    public static class ServerThread implements Runnable {

        @Override
        public void run() {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            final RaftServerHandler serverHandler = new RaftServerHandler();
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            //ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            //ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(4));
                            ChannelPipeline p = ch.pipeline();
                            p.addLast("decoder", new KryoDecoder());
                            p.addLast("encoder", new KryoEncoder());
                            p.addLast(serverHandler);
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            try {
                ChannelFuture f = bootstrap.bind(199).sync();
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new Thread(new ServerThread()).start();
        Thread.sleep(1000);
        test();
    }

    public static void test() throws Exception {
        NettyClient client = new NettyClient();
        client.init("127.0.0.1", 199);
        client.send(new AppendLogRequest(1, 1, 1, 1, null, 1));
    }
}
