package com.mtdhb.api;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.mtdhb.api.constant.ThirdPartyApplication;
import com.mtdhb.api.constant.ThreadPoolName;
import com.mtdhb.api.entity.Cookie;
import com.mtdhb.api.factory.NamedThreadFactory;

/**
 * @author i@huangdenghe.com
 * @date 2017/11/30
 */
@SpringBootApplication
@EnableScheduling
@EnableCaching
public class Application {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ThreadPoolExecutor asynSendMailPool() {
        // TODO 先用无界队列，崩了再说
        return new ThreadPoolExecutor(1, 1, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(),
                new NamedThreadFactory(ThreadPoolName.ASYN_SEND_MAIL_POOL));
    }

    @Bean
    public Map<String, Long> usage() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public List<LinkedBlockingQueue<Cookie>> queues() {
        // TODO 先用无界队列，崩了再说
        return Stream.of(ThirdPartyApplication.values()).map(application -> new LinkedBlockingQueue<Cookie>())
                .collect(Collectors.toList());
    }

    @Bean
    public ThreadPoolExecutor[] asynDispatchPools() {
        return Stream.of(ThirdPartyApplication.values())
                .map(application -> new ThreadPoolExecutor(1, 1, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(),
                        new NamedThreadFactory(ThreadPoolName.ASYN_DISPATCH_POOL + application.name())))
                .toArray(ThreadPoolExecutor[]::new);
    }

    @Bean
    public ThreadPoolExecutor[] asynReceivePools() {
        // TODO 先用无界队列，崩了再说
        return Stream.of(ThirdPartyApplication.values()).map(application -> {
            int poolSize = application.ordinal() + 1 << 2;
            return new ThreadPoolExecutor(poolSize, poolSize, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<>(),
                    new NamedThreadFactory(ThreadPoolName.ASYN_RECEIVE_POOL + application.name()));
        }).toArray(ThreadPoolExecutor[]::new);
    }

    @Bean
    public AtomicLong[] endpoints() {
        return Stream.of(ThirdPartyApplication.values()).map(application -> new AtomicLong())
                .toArray(AtomicLong[]::new);
    }

    @Bean
    public int[] thresholds() {
        // 美团拼手气红包最多20个，饿了么10个
        return new int[] { 20, 10 };
    }

    @Bean
    public BigDecimal[] mins() {
        // 美团拼手气红包的手气最佳红包的最小金额3.6，饿了么4.6
        return new BigDecimal[] { new BigDecimal("3.3"), new BigDecimal("4.6") };
    }

    @Resource(name = "asynDispatchPools")
    private ThreadPoolExecutor[] asynDispatchPools;

    @Resource(name = "asynReceivePools")
    private ThreadPoolExecutor[] asynReceivePools;

    @PreDestroy
    public void preDestroy() {
        // TODO 待优化
        // 关闭调度线程池
        Stream.of(asynDispatchPools).forEach(asynDispatchPool -> {
            List<Runnable> tasks = asynDispatchPool.shutdownNow();
            logger.info("DispatchTasks#size={}", tasks.size());
            try {
                asynDispatchPool.awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        });
        // 关闭领取线程池
        Stream.of(asynReceivePools).forEach(asynReceivePool -> {
            List<Runnable> tasks = asynReceivePool.shutdownNow();
            logger.info("ReceiveTasks#size={}", tasks.size());
            try {
                asynReceivePool.awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        });
    }

}
