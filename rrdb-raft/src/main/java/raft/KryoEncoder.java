package raft;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import kryo.KryoSerializer;

public class KryoEncoder extends MessageToByteEncoder<AppendLogRequest> {
	@Override
	protected void encode(ChannelHandlerContext ctx, AppendLogRequest message, ByteBuf out) throws Exception {
		KryoSerializer.serialize(message, out);
		ctx.flush();
	}
	
}
