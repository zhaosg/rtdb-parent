package raft;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RaftServiceImpl implements RaftService {

    public AppendLogResponse appendLog(AppendLogRequest appendLogRequest) {
        return new AppendLogResponse(1, 1);
    }

    public AppendLogResponse remoteAppendLog(Member member, AppendLogRequest appendLogRequest) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            //p.addLast(new LoggingHandler(LogLevel.INFO));
                            ch.pipeline().addLast("decoder", new KryoDecoder());
                            ch.pipeline().addLast("encoder", new KryoEncoder());
                            p.addLast(new EchoClientHandler());
                        }
                    });
            ChannelFuture f = b.connect(member.getHost(), member.getPort()).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
        return null;
    }
}