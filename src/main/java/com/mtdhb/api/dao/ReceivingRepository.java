package com.mtdhb.api.dao;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mtdhb.api.constant.e.ReceivingStatus;
import com.mtdhb.api.constant.e.ThirdPartyApplication;
import com.mtdhb.api.entity.Receiving;
import com.mtdhb.api.entity.view.ReceivingCarouselView;
import com.mtdhb.api.entity.view.ReceivingPieView;
import com.mtdhb.api.entity.view.ReceivingTrendView;

/**
 * @author i@huangdenghe.com
 * @date 2018/03/10
 */
public interface ReceivingRepository extends CrudRepository<Receiving, Long> {

    Receiving findByIdAndUserId(long id, long userId);

    Receiving findByApplicationAndStatusAndUserId(ThirdPartyApplication application, ReceivingStatus status,
            long userId);

    List<Receiving> findByUrlKeyAndApplicationAndStatus(String urlKey, ThirdPartyApplication application,
            ReceivingStatus status);

    Slice<Receiving> findByUserId(Long userId, Pageable pageable);

    List<Receiving> findByStatus(ReceivingStatus status);

    @Query("select u.mail as mail, r.application as application, r.price as price, r.gmtModified as gmtModified"
            + " from Receiving r, User u where r.userId=u.id and r.status=1 and r.price>0")
    Slice<ReceivingCarouselView> findReceivingCarouselView(Pageable pageable);

    @Query("select date_format(r.gmtCreate, '%Y/%m/%d') as date, sum(r.price) as totalPrice, count(*) as count "
            + "from Receiving r where r.status=1 and r.application=?1 and r.price>0 and r.gmtCreate>?2 "
            + "group by date_format(r.gmtCreate, '%Y/%m/%d') order by date_format(r.gmtCreate, '%Y/%m/%d') desc")
    List<ReceivingTrendView> findReceivingTrendView(ThirdPartyApplication application, Timestamp gmtCreate);

    @Query("select r.price as price, count(*) as count "
            + "from Receiving r where r.status=1 and r.application=?1 and r.price>0 and r.gmtCreate>?2 "
            + "group by r.price order by r.price")
    List<ReceivingPieView> findReceivingPieView(ThirdPartyApplication application, Timestamp gmtCreate);

}
