package com.study.netty.client;

import com.study.netty.client.codec.OrderFrameDecoder;
import com.study.netty.client.codec.OrderFrameEncoder;
import com.study.netty.client.codec.OrderProtocolDecoder;
import com.study.netty.client.codec.OrderProtocolEncoder;
import com.study.netty.client.handler.dispatcher.ClientIdleCheckHandler;
import com.study.netty.client.handler.dispatcher.KeepAliveHandler;
import com.study.netty.common.RequestMessage;
import com.study.netty.common.auth.AuthOperation;
import com.study.netty.common.order.OrderOperation;
import com.study.netty.common.util.IdUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLException;

/**
 * @author ldb
 * @date 2019-12-28 19:46
 * @dsscription
 */
public class Client0 {

    public static void main(String[] args) throws InterruptedException, SSLException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        // 客户端连接服务器最大允许时间  默认30秒
        bootstrap.option(NioChannelOption.CONNECT_TIMEOUT_MILLIS, 10 * 1000);

        SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();
        //下面这行，先直接信任自签证书，以避免没有看到ssl那节课程的同学运行不了；
        //学完ssl那节后，可以去掉下面这行代码，安装证书，安装方法参考课程，执行命令参考resources/ssl.txt里面
//        sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);

        SslContext sslContext = sslContextBuilder.build();

        NioEventLoopGroup group = new NioEventLoopGroup();
        try{
            bootstrap.group(group);

            KeepAliveHandler keepAliveHandler = new KeepAliveHandler();

            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new ClientIdleCheckHandler());

                    pipeline.addLast(sslContext.newHandler(ch.alloc()));
                    pipeline.addLast(new OrderFrameDecoder());
                    pipeline.addLast(new OrderFrameEncoder());
                    pipeline.addLast(new OrderProtocolEncoder());
                    pipeline.addLast(new OrderProtocolDecoder());

                    pipeline.addLast(keepAliveHandler);
                    pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                }
            });

            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8090);

            channelFuture.sync();

            RequestMessage authMsg = new RequestMessage(IdUtil.nextId(), new AuthOperation("admin","pwd"));
            channelFuture.channel().writeAndFlush(authMsg);
            RequestMessage requestMessage = new RequestMessage(IdUtil.nextId(), new OrderOperation(1001, "tudou"));

            for (int i = 0; i < 1; i++) {
                channelFuture.channel().writeAndFlush(requestMessage);
            }
            System.out.println("send ok");

            channelFuture.channel().closeFuture().sync();

        } finally {
            group.shutdownGracefully();
        }
    }
}
