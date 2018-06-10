package com.mtdhb.api.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.mtdhb.api.entity.CookieMark;

/**
 * @author i@huangdenghe.com
 * @date 2018/03/13
 */
public interface CookieMarkRepository extends CrudRepository<CookieMark, Long> {

    List<CookieMark> findByCookieIdAndUserId(long cookieId, long userId);

}
