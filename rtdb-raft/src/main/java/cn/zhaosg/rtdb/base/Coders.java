package cn.zhaosg.rtdb.base;

import cn.zhaosg.rtdb.raft.AppendLogRequest;
import cn.zhaosg.rtdb.raft.AppendLogResponse;
import cn.zhaosg.rtdb.serializers.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.List;

public class Coders {

    public static class Decoder extends ByteToMessageDecoder {

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            Object obj = KryoSerializer.deserialize(in);
            if (obj != null)
                out.add(obj);
        }
    }

    public static class AppendLogRequestEncoder extends MessageToByteEncoder<AppendLogRequest> {
        @Override
        protected void encode(ChannelHandlerContext ctx, AppendLogRequest message, ByteBuf out) throws Exception {
            KryoSerializer.serialize(message, out);
            ctx.flush();
        }
    }

    public static class AppendLogResponseEncoder extends MessageToByteEncoder<AppendLogResponse> {
        @Override
        protected void encode(ChannelHandlerContext ctx, AppendLogResponse message, ByteBuf out) throws Exception {
            KryoSerializer.serialize(message, out);
            ctx.flush();
        }
    }
}
