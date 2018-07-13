package com.mtdhb.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mtdhb.api.dao.CookieCountRepository;
import com.mtdhb.api.service.CookieCountService;

/**
 * @author i@huangdenghe.com
 * @date 2018/05/13
 */
@Service
public class CookieCountServiceImpl implements CookieCountService {

    @Autowired
    private CookieCountRepository cookieCountRepository;

    @Override
    public void deleteAll() {
        cookieCountRepository.deleteAll();
    }

}
