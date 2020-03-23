package com.study.netty.client;

import com.study.netty.client.codec.*;
import com.study.netty.client.handler.dispatcher.OperationResultFuture;
import com.study.netty.client.handler.dispatcher.RequestPendingCenter;
import com.study.netty.client.handler.dispatcher.ResponseDispatcherHandler;
import com.study.netty.common.OperationResult;
import com.study.netty.common.RequestMessage;
import com.study.netty.common.order.OrderOperation;
import com.study.netty.common.util.IdUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.internal.UnstableApi;

import java.util.concurrent.ExecutionException;

/**
 * @author ldb
 * @date 2019-12-28 19:46
 * @dsscription
 */
@UnstableApi
public class Client2 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);

        NioEventLoopGroup group = new NioEventLoopGroup();
        try{
            bootstrap.group(group);
            RequestPendingCenter requestPendingCenter = new RequestPendingCenter();
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new OrderFrameDecoder());
                    pipeline.addLast(new OrderFrameEncoder());
                    pipeline.addLast(new OrderProtocolEncoder());
                    pipeline.addLast(new OrderProtocolDecoder());
                    pipeline.addLast(new ResponseDispatcherHandler(requestPendingCenter));
                    pipeline.addLast(new OperationToRequestMessageEncoder());
                    pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                }
            });

            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8090);

            channelFuture.sync();

            long streamId = IdUtil.nextId();

            RequestMessage requestMessage = new RequestMessage(
                    streamId, new OrderOperation(1001, "tudou"));

            OperationResultFuture operationResultFuture = new OperationResultFuture();
            requestPendingCenter.add(streamId, operationResultFuture);

            channelFuture.channel().writeAndFlush(requestMessage);

            OperationResult operationResult = operationResultFuture.get();

            System.out.println(operationResult);

            channelFuture.channel().closeFuture().sync();

        } finally {
            group.shutdownGracefully();
        }
    }
}
