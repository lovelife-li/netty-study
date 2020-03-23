package com.study.netty.server;

import com.study.netty.server.codec.OrderFrameDecoder;
import com.study.netty.server.codec.OrderFrameEncoder;
import com.study.netty.server.codec.OrderProtocolDecoder;
import com.study.netty.server.codec.OrderProtocolEncoder;
import com.study.netty.server.hanler.AuthHandler;
import com.study.netty.server.hanler.MetricHandler;
import com.study.netty.server.hanler.OrderServerProcessHandler;
import com.study.netty.server.hanler.ServerIdleCheckHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.flush.FlushConsolidationHandler;
import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import io.netty.handler.ipfilter.RuleBasedIpFilter;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 * @author ldb
 * @date 2019-12-28 17:32
 * @dsscription
 */
@Slf4j
public class Server {
    public static void main(String[] args) throws InterruptedException, CertificateException, SSLException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(NioServerSocketChannel.class);

        // 最大的等待连接数量  默认128
        serverBootstrap.option(NioChannelOption.SO_BACKLOG, 1024);
        // 设置是否启用Nagle算法：用将小的碎片数据连接成更大的报文来提高发送效率。
        serverBootstrap.childOption(NioChannelOption.TCP_NODELAY, true);


        serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
        NioEventLoopGroup boss = new NioEventLoopGroup(0, new DefaultThreadFactory("boss"));
        NioEventLoopGroup work = new NioEventLoopGroup(0, new DefaultThreadFactory("work"));


        // buiness
        UnorderedThreadPoolEventExecutor businessGroup = new UnorderedThreadPoolEventExecutor(10, new DefaultThreadFactory(
                "business"));
        NioEventLoopGroup eventLoopGroupForTrafficShaping = new NioEventLoopGroup(0, new DefaultThreadFactory("TS"));

        try {
            serverBootstrap.group(boss, work);
            LoggingHandler loggingHandler = new LoggingHandler(LogLevel.INFO);

            //ipfilter
            IpSubnetFilterRule ipSubnetFilterRule = new IpSubnetFilterRule("127.1.1.1", 16, IpFilterRuleType.REJECT);
            RuleBasedIpFilter ruleBasedIpFilter = new RuleBasedIpFilter(ipSubnetFilterRule);

            // metric
            MetricHandler metricHandler = new MetricHandler();

            //trafficShaping，读写限制10M
            GlobalTrafficShapingHandler globalTrafficShapingHandler = new GlobalTrafficShapingHandler(eventLoopGroupForTrafficShaping, 10 * 1024 * 1024, 10 * 1024 * 1024);

            // auth
            AuthHandler authHandler = new AuthHandler();

            //ssl
            SelfSignedCertificate selfSignedCertificate = new SelfSignedCertificate();
            log.info("certificate position:" + selfSignedCertificate.certificate().toString());
            SslContext sslContext = SslContextBuilder.forServer(selfSignedCertificate.certificate(), selfSignedCertificate.privateKey()).build();


            serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    pipeline.addLast("ipFilter",ruleBasedIpFilter);

                    pipeline.addLast("metric", metricHandler);

                    pipeline.addLast("tsHandler", globalTrafficShapingHandler);

                    pipeline.addLast("idleCheck",new ServerIdleCheckHandler());

                    pipeline.addLast("ssl", sslContext.newHandler(ch.alloc()));

                    pipeline.addLast("orderFrameDecoder", new OrderFrameDecoder());
                    pipeline.addLast("OrderFrameEncoder", new OrderFrameEncoder());
                    pipeline.addLast("OrderProtocolEncoder", new OrderProtocolEncoder());
                    pipeline.addLast("OrderProtocolDecoder", new OrderProtocolDecoder());
                    pipeline.addLast("authHandler",authHandler);
                    pipeline.addLast(loggingHandler);

                    // 增加吞吐量，不是每次读都flush,读10次flush, consolidateWhenNoReadInProgress表示异步支持
                    pipeline.addLast("flushEnhance", new FlushConsolidationHandler(10, true));

                    pipeline.addLast(businessGroup, new OrderServerProcessHandler());
                }
            });

            ChannelFuture channelFuture = serverBootstrap.bind(8090).sync();

            channelFuture.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }
}
