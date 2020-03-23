package com.study.netty.common.order;

import com.study.netty.common.OperationResult;
import lombok.Data;

/**
 * @author ldb
 * @date 2019-12-28 16:55
 * @dsscription
 */
@Data
public class OrderOperationResult extends OperationResult {
    private final int tableId;
    private final String dish;
    private final boolean complete;

}
