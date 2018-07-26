package com.mtdhb.api.service;

import com.mtdhb.api.constant.e.CookieUseStatus;

/**
 * @author i@huangdenghe.com
 * @date 2018/05/13
 */
public interface CookieUseCountService {

    void deleteAll();

    void deleteByStatus(CookieUseStatus status);

}
