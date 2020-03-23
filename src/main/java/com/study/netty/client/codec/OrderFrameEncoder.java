package com.study.netty.client.codec;

import io.netty.handler.codec.LengthFieldPrepender;

/**
 * @author ldb
 * @date 2019-12-28 17:34
 * @dsscription
 */
public class OrderFrameEncoder extends LengthFieldPrepender {
    public OrderFrameEncoder() {
        super(2);
    }
}
