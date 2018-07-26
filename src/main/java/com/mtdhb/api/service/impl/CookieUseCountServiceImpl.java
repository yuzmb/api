package com.mtdhb.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mtdhb.api.constant.e.CookieUseStatus;
import com.mtdhb.api.dao.CookieUseCountRepository;
import com.mtdhb.api.service.CookieUseCountService;

/**
 * @author i@huangdenghe.com
 * @date 2018/05/13
 */
@Service
public class CookieUseCountServiceImpl implements CookieUseCountService {

    @Autowired
    private CookieUseCountRepository cookieUseCountRepository;

    @Override
    public void deleteAll() {
        cookieUseCountRepository.deleteAll();
    }

    @Override
    public void deleteByStatus(CookieUseStatus status) {
        cookieUseCountRepository.deleteByStatus(status);
    }

}
