package raft;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class RaftServerHandler extends ChannelInboundHandlerAdapter {
    private final ThreadLocal<RaftService> holder = ThreadLocal.withInitial(() -> new RaftServiceImpl());

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof AppendLogRequest) {
            AppendLogRequest request = (AppendLogRequest) msg;
            AppendLogResponse response = holder.get().appendLog(request);
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
