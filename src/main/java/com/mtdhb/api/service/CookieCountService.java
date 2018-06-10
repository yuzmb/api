package com.mtdhb.api.service;

import java.sql.Timestamp;

/**
 * @author i@huangdenghe.com
 * @date 2018/05/13
 */
public interface CookieCountService {

    void delete();

    void delete(Timestamp gmtCreate);

}
