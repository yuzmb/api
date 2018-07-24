package com.mtdhb.api.task;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * @author i@huangdenghe.com
 * @date 2018/07/13
 */
@Slf4j
public class ShutdownTask implements Runnable {

    private String threadPoolName;
    private ExecutorService executorService;
    private long timeout;
    private TimeUnit unit;

    public ShutdownTask(String threadPoolName, ExecutorService executorService, long timeout, TimeUnit unit) {
        super();
        this.threadPoolName = threadPoolName;
        this.executorService = executorService;
        this.timeout = timeout;
        this.unit = unit;
    }

    @Override
    public void run() {
        List<Runnable> tasks = executorService.shutdownNow();
        log.info("{} shutdownNow tasks#size={}", threadPoolName, tasks.size());
        try {
            boolean isTerminated = executorService.awaitTermination(timeout, unit);
            log.info("{} shutdownNow isTerminated={}", threadPoolName, isTerminated);
        } catch (InterruptedException e) {
            log.error("threadPoolName={}", threadPoolName, e);
        }
    }

}
