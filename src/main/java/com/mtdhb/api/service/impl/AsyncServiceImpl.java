package com.mtdhb.api.service.impl;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mtdhb.api.constant.ThirdPartyApplication;
import com.mtdhb.api.entity.Cookie;
import com.mtdhb.api.entity.Receiving;
import com.mtdhb.api.service.AsyncService;
import com.mtdhb.api.service.ReceivingService;
import com.mtdhb.api.task.DispatchTask;
import com.mtdhb.api.task.ReceiveTask;
import com.mtdhb.api.task.SendMailTask;

/**
 * @author i@huangdenghe.com
 * @date 2018/06/30
 */
@Service
public class AsyncServiceImpl implements AsyncService {

    @Autowired
    private ReceivingService receivingService;
    @Resource(name = "dispatchPools")
    private ThreadPoolExecutor[] dispatchPools;
    @Resource(name = "receivePools")
    private ThreadPoolExecutor[] receivePools;
    @Resource(name = "sendMailPool")
    private ThreadPoolExecutor sendMailPool;

    @Override
    public void sendMail(String to, String subject, String content) {
        sendMailPool.execute(new SendMailTask(to, subject, content));
    }

    @Override
    public void dispatch(Receiving receiving, long available) {
        ThirdPartyApplication application = receiving.getApplication();
        DispatchTask dispatchTask = new DispatchTask(receivingService, receiving, available);
        dispatchPools[application.ordinal()].execute(dispatchTask);
    }

    @Override
    public void receive(Receiving receiving, List<Cookie> cookies, long available) {
        ThirdPartyApplication application = receiving.getApplication();
        ReceiveTask receiveTask = new ReceiveTask(receivingService, receiving, cookies, available);
        receivePools[application.ordinal()].execute(receiveTask);
    }

}
