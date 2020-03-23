package com.study.netty.common.order;

import com.study.netty.common.Operation;
import com.study.netty.common.OperationResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ldb
 * @date 2019-12-28 16:53
 * @dsscription
 */
@Data
@Slf4j
public class OrderOperation extends Operation {

    private int tableId;
    private String dish;

    public OrderOperation(int tableId, String dish) {
        this.tableId = tableId;
        this.dish = dish;
    }

    @Override
    public OperationResult execute() {
        log.info("order's executing startup with orderRequest: " + toString());
        //execute order logic
//        Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
        log.info("order's executing complete");
        OrderOperationResult orderResponse = new OrderOperationResult(tableId, dish, true);
        return orderResponse;
    }
}
