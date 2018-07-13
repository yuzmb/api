package com.mtdhb.api.service.impl;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mtdhb.api.constant.ThreadPoolNames;
import com.mtdhb.api.constant.e.ThirdPartyApplication;
import com.mtdhb.api.entity.Cookie;
import com.mtdhb.api.entity.Receiving;
import com.mtdhb.api.factory.NamedThreadFactory;
import com.mtdhb.api.service.AsyncService;
import com.mtdhb.api.service.ReceivingService;
import com.mtdhb.api.task.DispatchTask;
import com.mtdhb.api.task.ReceiveTask;
import com.mtdhb.api.task.SendMailTask;
import com.mtdhb.api.task.ShutdownTask;

/**
 * @author i@huangdenghe.com
 * @date 2018/06/30
 */
@Service
public class AsyncServiceImpl implements AsyncService {

    @Autowired
    private ReceivingService receivingService;
    @Resource(name = "dispatchThreadPools")
    private ThreadPoolExecutor[] dispatchThreadPools;
    @Resource(name = "receiveThreadPools")
    private ThreadPoolExecutor[] receiveThreadPools;
    @Resource(name = "sendMailThreadPool")
    private ThreadPoolExecutor sendMailThreadPool;

    @Override
    public void sendMail(String to, String subject, String content) {
        sendMailThreadPool.execute(new SendMailTask(to, subject, content));
    }

    @Override
    public void dispatch(Receiving receiving, long available) {
        ThirdPartyApplication application = receiving.getApplication();
        DispatchTask dispatchTask = new DispatchTask(receivingService, receiving, available);
        dispatchThreadPools[application.ordinal()].execute(dispatchTask);
    }

    @Override
    public void receive(Receiving receiving, List<Cookie> cookies, long available) {
        ThirdPartyApplication application = receiving.getApplication();
        ReceiveTask receiveTask = new ReceiveTask(receivingService, receiving, cookies, available);
        receiveThreadPools[application.ordinal()].execute(receiveTask);
    }

    @Override
    public void destroy(long timeout, TimeUnit unit) throws InterruptedException {
        int poolSize = 1 + dispatchThreadPools.length + receiveThreadPools.length;
        CountDownLatch countDownLatch = new CountDownLatch(poolSize);
        ThreadPoolExecutor shutdownThreadPool = new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new NamedThreadFactory(ThreadPoolNames.SHUTDOWN_THREAD_POOL));
        shutdownThreadPool
                .execute(new ShutdownTask(ThreadPoolNames.SEND_MAIL_THREAD_POOL, sendMailThreadPool, countDownLatch));
        Stream.of(ThirdPartyApplication.values()).forEach(application -> {
            String applicationName = application.name();
            int applicationOrdinal = application.ordinal();
            shutdownThreadPool.execute(new ShutdownTask(applicationName + ThreadPoolNames.DISPATCH_THREAD_POOL,
                    dispatchThreadPools[applicationOrdinal], countDownLatch));
            shutdownThreadPool.execute(new ShutdownTask(applicationName + ThreadPoolNames.RECEIVE_THREAD_POOL,
                    receiveThreadPools[applicationOrdinal], countDownLatch));
        });
        countDownLatch.await(timeout, unit);
        shutdownThreadPool.shutdownNow();
        shutdownThreadPool.awaitTermination(timeout, unit);
    }

}
