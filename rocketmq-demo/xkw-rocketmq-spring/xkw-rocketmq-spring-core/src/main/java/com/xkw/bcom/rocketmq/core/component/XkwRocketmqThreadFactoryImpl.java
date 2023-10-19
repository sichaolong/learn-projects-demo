/*
 * xkw.com Inc. Copyright (c) 2023 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.core.component;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * XkwRocketmqThreadFactoryImpl
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2023年05月09日
 */
public class XkwRocketmqThreadFactoryImpl implements ThreadFactory {

    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final String namePrefix;

    public XkwRocketmqThreadFactoryImpl(String namePrefix) {
        this.namePrefix = namePrefix;
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}
