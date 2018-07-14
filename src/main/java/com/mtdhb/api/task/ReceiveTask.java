package com.mtdhb.api.task;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mtdhb.api.entity.Cookie;
import com.mtdhb.api.entity.Receiving;
import com.mtdhb.api.service.ReceivingService;

/**
 * @author i@huangdenghe.com
 * @date 2018/04/08
 */
public class ReceiveTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private ReceivingService receivingService;
    private Receiving receiving;
    private List<Cookie> cookies;
    private long available;

    public ReceiveTask(ReceivingService receivingService, Receiving receiving, List<Cookie> cookies, long available) {
        this.receivingService = receivingService;
        this.receiving = receiving;
        this.cookies = cookies;
        this.available = available;
    }

    @Override
    public void run() {
        logger.info("ReceiveTask#run starting...");
        receivingService.receive(receiving, cookies, available);
        logger.info("ReceiveTask#run end");
    }

}
