package com.study.netty.server.hanler;

import com.study.netty.common.Operation;
import com.study.netty.common.RequestMessage;
import com.study.netty.common.auth.AuthOperation;
import com.study.netty.common.auth.AuthOperationResult;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ldb
 * @date 2020-01-30 16:20
 * @dsscription
 */
@Slf4j
@ChannelHandler.Sharable
public class AuthHandler extends SimpleChannelInboundHandler<RequestMessage> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMessage requestMessage) throws Exception {
        try {
            Operation operation = requestMessage.getMessageBody();
            if (operation instanceof AuthOperation){
                AuthOperation authOperation = AuthOperation.class.cast(operation);
                AuthOperationResult result = authOperation.execute();
                if (result.isPassAuth()){
                    log.info("pass auth");
                }else {
                    log.error("fail to auth");
                    ctx.close();
                }
            }else {
                log.error("first msg  is not auth");
                ctx.close();
            }
        } catch (Exception e) {
            log.error("exception happen for: " + e.getMessage(), e);
            ctx.close();
        } finally {
            ctx.pipeline().remove(this);
        }
    }
}
