/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package cn.zhaosg.rtdb.netty;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import cn.zhaosg.rtdb.raft.AppendLogRequest;
import cn.zhaosg.rtdb.raft.AppendLogResponse;
import cn.zhaosg.rtdb.raft.RaftService;
import cn.zhaosg.rtdb.raft.RaftServiceImpl;

/**
 * Handler implementation for the echo server.
 */
@Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    private final ThreadLocal<RaftService> holder = ThreadLocal.withInitial(() -> new RaftServiceImpl());

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("xxxxxxxxxxxxxxxxx");//
        ctx.write(msg);

        if (msg instanceof AppendLogRequest) {
            AppendLogRequest request = (AppendLogRequest) msg;
            AppendLogResponse response = holder.get().appendLog(request);
            ctx.writeAndFlush(response);
        }
    }
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//        ctx.write(msg);
//    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
