package com.mtdhb.api.service;

import java.util.List;

import com.mtdhb.api.entity.Cookie;
import com.mtdhb.api.entity.Receiving;

/**
 * @author i@huangdenghe.com
 * @date 2018/06/30
 */
public interface AsyncService {

    void sendMail(String to, String subject, String content);

    void dispatch(Receiving receiving, long available);

    void receive(Receiving receiving, List<Cookie> cookies, long available);

}
