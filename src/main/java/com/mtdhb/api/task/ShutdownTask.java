package com.mtdhb.api.task;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.CountDownLatch;
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
    private CountDownLatch countDownLatch;

    public ShutdownTask(String threadPoolName, ExecutorService executorService, CountDownLatch countDownLatch) {
        this.threadPoolName = threadPoolName;
        this.executorService = executorService;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        logger.info("ShutdownTask#run starting...");
        List<Runnable> tasks = executorService.shutdownNow();
        logger.info("{} tasks#size={}", threadPoolName, tasks.size());
        try {
            executorService.awaitTermination(1, TimeUnit.MINUTES);
            countDownLatch.countDown();
        } catch (InterruptedException e) {
            logger.error("threadPoolName={}", threadPoolName, e);
        }
        logger.info("ShutdownTask#run end");
    }

}
