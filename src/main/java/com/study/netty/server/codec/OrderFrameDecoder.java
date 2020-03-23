package com.study.netty.server.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author ldb
 * @date 2019-12-28 17:34
 * @dsscription
 */
public class OrderFrameDecoder extends LengthFieldBasedFrameDecoder {

    public OrderFrameDecoder() {
        super(Integer.MAX_VALUE, 0, 2, 0, 2);
    }
}
