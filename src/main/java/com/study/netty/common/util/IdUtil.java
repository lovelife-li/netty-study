package com.study.netty.common.util;

import java.util.concurrent.atomic.AtomicLong;

public final class IdUtil {

    private static final AtomicLong IDX = new AtomicLong();

    private IdUtil(){
        //no instance
    }

    public static long nextId(){
        return IDX.incrementAndGet();
    }

    public static void main(String[] args) {
        System.out.println(IdUtil.nextId());
    }
}
