package com.mtdhb.api.task;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mtdhb.api.constant.e.ThirdPartyApplication;
import com.mtdhb.api.entity.Cookie;
import com.mtdhb.api.service.CookieCountService;
import com.mtdhb.api.service.CookieService;

@Component
public class ScheduleTask {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    private CookieService cookieService;
    @Autowired
    private CookieCountService cookieCountService;
    @Resource(name = "usage")
    private Map<Long, Long> usage;
    @Resource(name = "queues")
    private List<LinkedBlockingQueue<Cookie>> queues;
    @Resource(name = "endpoints")
    private AtomicLong[] endpoints;

    @Scheduled(cron = "0 0 0 * * ?")
    public void reload() {
        logger.info("Reload starting...");
        usage.clear();
        Stream.of(ThirdPartyApplication.values()).forEach(application -> {
            queues.get(application.ordinal()).clear();
            endpoints[application.ordinal()].set(0L);
            cookieService.load(application);
        });
    }

    @Scheduled(cron = "0 55 23 * * ?")
    public void clear() {
        logger.info("Clear starting...");
        cookieCountService.delete();
    }

}
