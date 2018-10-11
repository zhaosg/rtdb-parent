package cn.zhaosg.rtdb.base;

import cn.zhaosg.rtdb.Cfg;
import cn.zhaosg.rtdb.core.AppendLogRequest;
import cn.zhaosg.rtdb.core.AppendLogResponse;
import cn.zhaosg.rtdb.core.RaftService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server implements Runnable {
    public Server() {
    }

    public Server(int port) {
    }

    public void start() {
        new Thread(this).start();
    }

    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        //ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                        //ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(4));
                        ChannelPipeline p = ch.pipeline();
                        p.addLast("decoder", new Coders.Decoder());
                        p.addLast("encoderRequest", new Coders.AppendLogRequestEncoder());
                        p.addLast("encoderResponse", new Coders.AppendLogResponseEncoder());
                        p.addLast(new RaftServerHandler());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        try {
            ChannelFuture f = bootstrap.bind(Cfg.port()).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public class RaftServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            if (msg instanceof AppendLogRequest) {
                AppendLogRequest request = (AppendLogRequest) msg;
                AppendLogResponse response = RaftService.instance().appendLog(request);
                ctx.writeAndFlush(response);
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
