package com.study.netty.client.handler.dispatcher;

import com.study.netty.common.OperationResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ldb
 * @date 2019-12-28 20:31
 * @dsscription
 */
public class RequestPendingCenter {
    private Map<Long, OperationResultFuture> map = new ConcurrentHashMap<>();

    public void add(Long streamId, OperationResultFuture future){
        this.map.put(streamId, future);
    }

    public void set(Long streamId, OperationResult operationResult){
        OperationResultFuture operationResultFuture = this.map.get(streamId);
        if (operationResultFuture != null) {
            operationResultFuture.setSuccess(operationResult);
            this.map.remove(streamId);
        }
    }
}
