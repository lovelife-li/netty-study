package com.study.netty.client.codec;

import com.study.netty.common.Operation;
import com.study.netty.common.RequestMessage;
import com.study.netty.common.util.IdUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @author ldb
 * @date 2019-12-28 20:10
 * @dsscription
 */
public class OperationToRequestMessageEncoder extends MessageToMessageEncoder<Operation> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Operation operation, List<Object> out) throws Exception {
        RequestMessage requestMessage = new RequestMessage(IdUtil.nextId(), operation);
        out.add(requestMessage);
    }
}
