package com.mtdhb.api.task;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author i@huangdenghe.com
 * @date 2018/07/13
 */
public class ShutdownTask implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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
        logger.info("ShutdownTask#run starting...");
        List<Runnable> tasks = executorService.shutdownNow();
        logger.info("{} tasks#size={}", threadPoolName, tasks.size());
        try {
            boolean isTerminated = executorService.awaitTermination(timeout, unit);
            logger.info("{} shutdownNow isTerminated={}", threadPoolName, isTerminated);
        } catch (InterruptedException e) {
            logger.error("threadPoolName={}", threadPoolName, e);
        }
        logger.info("ShutdownTask#run end");
    }

}
