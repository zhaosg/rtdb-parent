package cn.zhaosg.rtdb.base;

import cn.zhaosg.rtdb.raft.AppendEntriesRequest;
import cn.zhaosg.rtdb.raft.AppendEntriesResponse;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Promise;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;

public class NettyClient {
    private Logger logger = LoggerFactory.getLogger(NettyClient.class);
    GenericObjectPool<Channel> objectPool;

    public NettyClient(String host, int port) {
        ChannelFactory factory = new ChannelFactory(host, port);
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMinIdle(1);
        poolConfig.setMaxIdle(5);
        poolConfig.setMaxTotal(10);
        objectPool = new GenericObjectPool<Channel>(factory, poolConfig);
    }

    final Map<Channel, Consumer<Object>> map = new ConcurrentHashMap<>();

    public <T> void send(Object request, Class<T> cls, Consumer<T> dataReady) {
        final ChannelHolder channelHolder = new ChannelHolder();
        try {
            channelHolder.channel = objectPool.borrowObject(200);
            map.put(channelHolder.channel, (data) -> {
                dataReady.accept((T) data);
                if (channelHolder.channel != null)
                    objectPool.returnObject(channelHolder.channel);
            });
            ChannelFuture sendFuture = channelHolder.channel.writeAndFlush(request);
            sendFuture.addListener(cf -> {
                try {
                    if (!cf.isSuccess()) {
                        logger.error("", cf.cause());
                    }
                } catch (Throwable t) {
                    logger.error("", t);
                    if (channelHolder.channel != null)
                        objectPool.returnObject(channelHolder.channel);
                } finally {

                }
            });
        } catch (Exception e) {
            logger.error("", e);
            if (channelHolder.channel != null)
                objectPool.returnObject(channelHolder.channel);
        }
    }

    public void close() {
        objectPool.close();
    }

    class ChannelHolder {
        public Channel channel;
    }

    public class ResponseHandler extends ChannelInboundHandlerAdapter {
        private Consumer<Object> dataReady;

        public ResponseHandler() {

        }

        public ResponseHandler(Consumer<Object> dataReady) {
            this.dataReady = dataReady;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            map.get(ctx.channel()).accept(msg);
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

    public class ChannelFactory implements PooledObjectFactory<Channel> {
        private Logger logger = LoggerFactory.getLogger(NettyClient.class);
        private EventLoopGroup group;
        private String host;
        private int port;

        public ChannelFactory() {
        }

        public ChannelFactory(String host, int port) {
            this.host = host;
            this.port = port;
            group = new NioEventLoopGroup(5, new ThreadFactoryBuilder()
                    .setNameFormat("drift-client-%s")
                    .setDaemon(true)
                    .build());
        }

        /**
         * 功能描述：激活资源对象
         * <p>
         * 什么时候会调用此方法
         * 1：从资源池中获取资源的时候
         * 2：资源回收线程，回收资源的时候，根据配置的 testWhileIdle 参数，判断 是否执行 factory.activateObject()方法，true 执行，false 不执行
         *
         * @param arg0
         */
        @Override
        public void activateObject(PooledObject<Channel> arg0) throws Exception {
//            System.out.println("activate Object");
        }

        @Override
        public void destroyObject(PooledObject<Channel> arg0) throws Exception {
            Channel channel = arg0.getObject();
            channel.close();
            map.remove(channel);
            channel = null;
        }


        @Override
        public PooledObject<Channel> makeObject() throws Exception {
            final ChannelHolder channelHolder = new ChannelHolder();
            final CountDownLatch latch = new CountDownLatch(1);
            Bootstrap b = new Bootstrap();
            final ResponseHandler hander = new ResponseHandler();
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
                        channelHolder.channel = (Channel) channelFuture.getNow();
                        latch.countDown();
                    } else {
                        logger.error("", channelFuture.cause());
                    }
                } catch (Throwable t) {
                    logger.error("", t);
                }
            });
            latch.await();
            return new DefaultPooledObject<>(channelHolder.channel);
        }

        /**
         * 功能描述：钝化资源对象
         * <p>
         * 什么时候会调用此方法
         * 1：将资源返还给资源池时，调用此方法。
         */
        @Override
        public void passivateObject(PooledObject<Channel> channelPooledObject) throws Exception {
//            channelPooledObject.getObject().close();
        }

        /**
         * 功能描述：判断资源对象是否有效，有效返回 true，无效返回 false
         * <p>
         * 什么时候会调用此方法
         * 1：从资源池中获取资源的时候，参数 testOnBorrow 或者 testOnCreate 中有一个 配置 为 true 时，则调用  factory.validateObject() 方法
         * 2：将资源返还给资源池的时候，参数 testOnReturn，配置为 true 时，调用此方法
         * 3：资源回收线程，回收资源的时候，参数 testWhileIdle，配置为 true 时，调用此方法
         */
        @Override
        public boolean validateObject(PooledObject<Channel> channelPooledObject) {
            return channelPooledObject.getObject().isOpen() && channelPooledObject.getObject().isActive();
        }

        private void notifyConnect(ChannelFuture future, Promise<Channel> promise) {
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


    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            NettyClient connection = new NettyClient("127.0.0.1", 1987);
            for (int i = 0; i < 300000; i++) {
                AppendEntriesRequest request = new AppendEntriesRequest(i, 1, 1, 1, null, 1);
                final int ii = i;
                connection.send(request, AppendEntriesResponse.class, (result) -> {
                    if (ii != result.getTerm())
                        System.out.println("" + result.getTerm() + " is bad");
                });
            }
            connection.close();
        });
        thread.start();
    }
}
