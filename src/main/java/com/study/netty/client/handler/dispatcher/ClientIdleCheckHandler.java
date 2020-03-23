package com.study.netty.client.handler.dispatcher;

import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author ldb
 * @date 2020-01-29 21:37
 * @dsscription
 */
public class ClientIdleCheckHandler extends IdleStateHandler {

    public ClientIdleCheckHandler() {
        super(0, 5, 0, TimeUnit.SECONDS);
    }
}
