package com.styx.data.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author TontoZhou
 * @since 2020/4/16
 */
@Component
@Slf4j
public class NettyMessageServer {

    @Value("${netty.server.port:9000}")
    private int port;
    @Value("${netty.server.backlog:1024}")
    private int backlog;

    @Autowired
    private ProtocolOutboundHandler outboundHandler;
    @Autowired
    private ProtocolInboundHandler inboundHandler;

    public void start() throws Exception {
        final EventLoopGroup bossGroup = new NioEventLoopGroup();
        final EventLoopGroup workerGroup = new NioEventLoopGroup();

        int processorSize = Runtime.getRuntime().availableProcessors();
        final EventExecutorGroup eventExecutorGroup = new DefaultEventExecutorGroup(processorSize * 3);

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 按分隔符解析帧

                            /* 现在按照FBFA作为分隔符解码，而FB是前一帧的结尾，FA是后一帧的头，
                             * 所以会出现第一个数据帧的多出一个FA的问题,并且必须接受到后一帧才
                             * 能解码出前一帧，所以导致有一个数据帧永远在等待下一个FA出现。
                             *
                             * 需要协议升级解决
                             */

                            pipeline.addLast(new FixedLengthFrameDecoder(1024));
                            // 超时设置
                            pipeline.addLast(new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS));
                            // 协议出站处理器
                            pipeline.addLast(outboundHandler);
                            // 协议处理器，是否开启EventExecutorGroup，
                            // 如果需要开启业务线程，并且需要写操作，建议使用netty自带EventExecutorGroup
                            // 如果不需要写操作，可使用其他线程池作为业务线程池，并自行优化
                            pipeline.addLast(eventExecutorGroup, inboundHandler);
                        }
                    })
                    // 长时间没有消息接收时，发送探测帧
                    //.childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 客户端等待队列长度
                    .option(ChannelOption.SO_BACKLOG, backlog);


            final NettyMessageServer server = this;


            // 绑定端口，开始接收进来的连接
            ChannelFuture f = b.bind(port).sync();

            f.addListener((future) -> log.info("==== 数据采集服务启动成功 ===="));

//            Runtime.getRuntime().addShutdownHook(new Thread() {
//                @Override
//                public void run() {
//                    if (channel != null) {
//                        channel.close();
//                    }
//                    workerGroup.shutdownGracefully();
//                    bossGroup.shutdownGracefully();
//                }
//            });

            // 等待服务器  socket 关闭 。
            // 在这个例子中，这不会发生，但你可以优雅地关闭你的服务器。
            f.channel().closeFuture().syncUninterruptibly();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }


}
