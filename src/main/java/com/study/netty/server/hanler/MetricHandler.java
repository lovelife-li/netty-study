package com.study.netty.server.hanler;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

/**
 * @author ldb
 * @date 2020-01-29 11:09
 * @dsscription
 */
@ChannelHandler.Sharable
public class MetricHandler extends ChannelDuplexHandler {

    private volatile long totalConnection = 0;
    private static final AtomicLongFieldUpdater<MetricHandler> TOTAL_CONNECTION =
            AtomicLongFieldUpdater.newUpdater(MetricHandler.class, "totalConnection");

    {
        MetricRegistry metricRegistry = new MetricRegistry();
        metricRegistry.register("totalConnection", (Gauge<Long>) () -> totalConnection);

//        ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(metricRegistry).build();
//        consoleReporter.start(3, TimeUnit.SECONDS);
//
//        JmxReporter jmxReporter = JmxReporter.forRegistry(metricRegistry).build();
//        jmxReporter.start();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        TOTAL_CONNECTION.incrementAndGet(this);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        TOTAL_CONNECTION.decrementAndGet(this);
        super.channelInactive(ctx);
    }
}
