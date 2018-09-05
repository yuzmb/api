package com.mtdhb.api.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;

/**
 * @author i@huangdenghe.com
 * @date 2018/03/28
 * @see java.util.concurrent.Executors.DefaultThreadFactory
 */
public class NamedThreadFactory implements ThreadFactory {

    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
    private final ThreadGroup GROUP;
    private final AtomicInteger THREAD_NUMBER = new AtomicInteger(1);
    private final String NAME_PREFIX;
    private final LogUncaughtExceptionHandler HANDLER;

    public NamedThreadFactory(String name) {
        SecurityManager s = System.getSecurityManager();
        GROUP = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        if (null == name || name.isEmpty()) {
            name = "pool";
        }
        NAME_PREFIX = name + "-" + POOL_NUMBER.getAndIncrement() + "-thread-";
        HANDLER = new LogUncaughtExceptionHandler();
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(GROUP, r, NAME_PREFIX + THREAD_NUMBER.getAndIncrement(), 0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        t.setUncaughtExceptionHandler(HANDLER);
        return t;
    }

    @Slf4j
    static class LogUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            log.error(t.getName(), e);
        }

    }

}
