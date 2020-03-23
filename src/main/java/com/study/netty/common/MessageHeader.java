package com.study.netty.common;

import lombok.Data;

/**
 * @author ldb
 * @date 2019-12-28 16:31
 * @dsscription
 */
@Data
public class MessageHeader {

    private int version = 1;
    private int opCode;
    private long streamId;
}
