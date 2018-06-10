package com.mtdhb.api.task;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mtdhb.api.entity.Receiving;
import com.mtdhb.api.service.ReceivingService;

public class DispatchTask implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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
        logger.info("DispatchTask#run starting...");
        receivingService.dispatch(receiving, available);
        logger.info("DispatchTask#run end");
    }

}
