package com.test.weakreference;

import java.lang.ref.WeakReference;

/**
 * Salad class
 * 继承WeakReference，将Apple作为弱引用。
 * 注意到时候回收的是Apple，而不是Salad
 *
 * @author BrightLoong
 * @date 2018/5/25
 */
public class Salad extends WeakReference<Apple> {
    public Salad(Apple apple) {
        super(apple);
    }
}
