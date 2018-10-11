package cn.zhaosg.rtdb.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import cn.zhaosg.rtdb.raft.AppendLogRequest;
import cn.zhaosg.rtdb.serializers.kryo.KryoSerializer;

public class KryoEncoder extends MessageToByteEncoder<AppendLogRequest> {
	@Override
	protected void encode(ChannelHandlerContext ctx, AppendLogRequest message, ByteBuf out) throws Exception {
		KryoSerializer.serialize(message, out);
		ctx.flush();
	}
	
}
