package com.study.netty.common.keepalive;

import com.study.netty.common.OperationResult;
import lombok.Data;

@Data
public class KeepaliveOperationResult extends OperationResult {

    private final long time;

}
