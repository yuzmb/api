package com.mtdhb.api.task;

import com.mtdhb.api.entity.Receiving;
import com.mtdhb.api.service.ReceivingService;

/**
 * @author i@huangdenghe.com
 * @date 2018/07/14
 */
public class DispatchTask implements Runnable {

    private ReceivingService receivingService;
    private Receiving receiving;
    private long available;

    public DispatchTask(ReceivingService receivingService, Receiving receiving, long available) {
        this.receivingService = receivingService;
        this.receiving = receiving;
        this.available = available;
    }

    @Override
    public void run() {
        receivingService.dispatch(receiving, available);
    }

}
