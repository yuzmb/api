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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.mtdhb.api.constant.ThreadPoolNames;
import com.mtdhb.api.constant.e.ThirdPartyApplication;
import com.mtdhb.api.entity.Cookie;
import com.mtdhb.api.factory.NamedThreadFactory;
import com.mtdhb.api.service.AsyncService;

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
    public ThreadPoolExecutor sendMailThreadPool() {
        // TODO 先用无界队列，崩了再说
        return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
                new NamedThreadFactory(ThreadPoolNames.SEND_MAIL_THREAD_POOL));
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
    public ThreadPoolExecutor[] dispatchThreadPools() {
        // TODO 先用无界队列，崩了再说
        return Stream.of(ThirdPartyApplication.values())
                .map(application -> new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
                        new NamedThreadFactory(application.name() + ThreadPoolNames.DISPATCH_THREAD_POOL)))
                .toArray(ThreadPoolExecutor[]::new);
    }

    @Bean
    public ThreadPoolExecutor[] receiveThreadPools() {
        // TODO 先用无界队列，崩了再说
        return Stream.of(ThirdPartyApplication.values()).map(application -> {
            int poolSize = application.ordinal() + 1 << 2;
            return new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
                    new NamedThreadFactory(application.name() + ThreadPoolNames.RECEIVE_THREAD_POOL));
        }).toArray(ThreadPoolExecutor[]::new);
    }

    @Bean
    public AtomicLong[] endpoints() {
        return Stream.of(ThirdPartyApplication.values()).map(application -> new AtomicLong())
                .toArray(AtomicLong[]::new);
    }

    @Bean
    public int[] thresholds() {
        // 美团拼手气红包最多 20 个，饿了么 10 个
        return new int[] { 20, 10 };
    }

    @Bean
    public BigDecimal[] mins() {
        // 美团拼手气红包的手气最佳红包的最小金额 3.6，饿了么 4.6
        return new BigDecimal[] { new BigDecimal("3.3"), new BigDecimal("4.6") };
    }

    @Autowired
    private AsyncService asyncService;

    @PreDestroy
    public void preDestroy() {
        try {
            asyncService.destroy(90, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
