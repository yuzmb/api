package com.mtdhb.api.task;

import java.util.List;

import com.mtdhb.api.entity.Cookie;
import com.mtdhb.api.entity.Receiving;
import com.mtdhb.api.service.ReceivingService;

/**
 * @author i@huangdenghe.com
 * @date 2018/04/08
 */
public class ReceiveTask implements Runnable {

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
        receivingService.receive(receiving, cookies, available);
    }

}
