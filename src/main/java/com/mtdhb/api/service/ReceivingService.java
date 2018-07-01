package com.mtdhb.api.service;

import java.util.List;

import com.mtdhb.api.constant.ThirdPartyApplication;
import com.mtdhb.api.dto.ReceivingCarouselDTO;
import com.mtdhb.api.dto.ReceivingDTO;
import com.mtdhb.api.dto.ReceivingPieDTO;
import com.mtdhb.api.dto.ReceivingTrendDTO;
import com.mtdhb.api.entity.Cookie;
import com.mtdhb.api.entity.Receiving;

/**
 * @author i@huangdenghe.com
 * @date 2018/04/17
 */
public interface ReceivingService {

    ReceivingDTO get(long receivingId, long userId);

    List<ReceivingDTO> list(long userId);

    List<ReceivingCarouselDTO> listReceivingCarousel();

    List<ReceivingTrendDTO> listReceivingTrend(ThirdPartyApplication application);

    List<ReceivingPieDTO> listReceivingPie(ThirdPartyApplication application);

    ReceivingDTO save(String key, String url, String phone, ThirdPartyApplication application, long userId);

    void dispatch(Receiving receiving, long available);

    void receive(Receiving receiving, List<Cookie> cookies, long available);

}
