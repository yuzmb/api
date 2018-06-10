package com.mtdhb.api.dao;

import java.sql.Timestamp;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mtdhb.api.constant.ThirdPartyApplication;
import com.mtdhb.api.entity.CookieCount;
import com.mtdhb.api.entity.view.CookieCountView;

/**
 * @author i@huangdenghe.com
 * @date 2018/03/13
 */
public interface CookieCountRepository extends CrudRepository<CookieCount, Long> {

    long countByApplicationAndUserIdAndGmtCreateGreaterThan(ThirdPartyApplication application, long userId,
            Timestamp gmtCreate);

    @Query(value = "select ck.openId as openId, count(*) as count"
            + " from CookieCount ck where ck.application=?1 and ck.gmtCreate>?2 and ck.cookieId between ?3 and ?4 group by ck.openId")
    List<CookieCountView> findCookieCountView(ThirdPartyApplication application, Timestamp gmtCreate, long lower,
            long upper);

    @Transactional
    void deleteByGmtCreateLessThan(Timestamp gmtCreate);

}
