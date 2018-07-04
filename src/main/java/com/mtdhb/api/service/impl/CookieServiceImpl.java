package com.mtdhb.api.service.impl;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mtdhb.api.constant.e.ErrorCode;
import com.mtdhb.api.constant.e.HttpService;
import com.mtdhb.api.constant.e.ReceivingStatus;
import com.mtdhb.api.constant.e.ThirdPartyApplication;
import com.mtdhb.api.dao.CookieCountRepository;
import com.mtdhb.api.dao.CookieRepository;
import com.mtdhb.api.dao.ReceivingRepository;
import com.mtdhb.api.dto.CookieDTO;
import com.mtdhb.api.dto.CookieRankDTO;
import com.mtdhb.api.dto.nodejs.CookieCheckDTO;
import com.mtdhb.api.entity.Cookie;
import com.mtdhb.api.entity.Receiving;
import com.mtdhb.api.entity.view.CookieCountView;
import com.mtdhb.api.entity.view.CookieRankView;
import com.mtdhb.api.exception.BusinessException;
import com.mtdhb.api.service.CookieService;
import com.mtdhb.api.service.NodejsService;
import com.mtdhb.api.service.UserService;
import com.mtdhb.api.util.Entities;

/**
 * @author i@huangdenghe.com
 * @date 2018/04/15
 */
@Service
public class CookieServiceImpl implements CookieService {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final int CHUNK_SIZE = 1024;

    @Autowired
    private NodejsService nodejsService;
    @Autowired
    private UserService userService;
    @Autowired
    private CookieRepository cookieRepository;
    @Autowired
    private CookieCountRepository cookieCountRepository;
    @Autowired
    private ReceivingRepository receivingRepository;
    @Resource(name = "endpoints")
    private AtomicLong[] endpoints;
    @Resource(name = "queues")
    private List<LinkedBlockingQueue<Cookie>> queues;
    @Resource(name = "thresholds")
    private int[] thresholds;
    @Resource(name = "usage")
    private Map<String, Long> usage;

    @Override
    public List<CookieDTO> list(long userId) {
        List<Cookie> cookies = cookieRepository.findByUserId(userId);
        List<CookieDTO> cookieDTOs = cookies.stream().map(cookie -> {
            cookie.setNickname(Entities.decodeNickname(cookie.getNickname()));
            return cookie;
        }).map(cookie -> {
            CookieDTO cookieDTO = new CookieDTO();
            BeanUtils.copyProperties(cookie, cookieDTO);
            return cookieDTO;
        }).collect(Collectors.toList());
        return cookieDTOs;
    }

    @Cacheable(cacheNames = "COOKIE_RANK")
    @Override
    public List<CookieRankDTO> listCookieRank(ThirdPartyApplication application, long userId) {
        Page<CookieRankView> page = cookieRepository.findCookieRankViewByApplication(application,
                new PageRequest(0, 100));
        final AtomicInteger ranking = new AtomicInteger();
        List<CookieRankDTO> cookieRankDTOs = page.map(cookieRankView -> {
            CookieRankDTO cookieRankDTO = new CookieRankDTO();
            BeanUtils.copyProperties(cookieRankView, cookieRankDTO);
            cookieRankDTO.setRanking(ranking.incrementAndGet());
            return cookieRankDTO;
        }).getContent();
        return cookieRankDTOs;
    }

    @Override
    public void load(ThirdPartyApplication application) {
        logger.info("{} queue loading...", application);
        LinkedBlockingQueue<Cookie> queue = queues.get(application.ordinal());
        int threshold = thresholds[application.ordinal()];
        // 小于阈值要重新加载
        while (queue.size() < threshold) {
            AtomicLong endpoint = endpoints[application.ordinal()];
            long lower = endpoint.get();
            logger.info("application={}, lower={}", application, lower);
            Slice<Cookie> cookies = cookieRepository.findByApplicationAndIdGreaterThan(application, lower,
                    new PageRequest(0, CHUNK_SIZE));
            int numberOfElements = cookies.getNumberOfElements();
            logger.info("cookies#size={}", numberOfElements);
            if (numberOfElements < 1) {
                return;
            }
            Cookie last = cookies.getContent().get(numberOfElements - 1);
            long upper = last.getId();
            logger.info("upper={}", upper);
            Timestamp today = Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.ofSecondOfDay(0)));
            List<CookieCountView> cookieCountViews = cookieCountRepository.findCookieCountView(application, today,
                    lower, upper);
            logger.info("cookieCountViews#size={}", cookieCountViews.size());
            cookieCountViews.stream().forEach(
                    cookieCountView -> usage.putIfAbsent(cookieCountView.getOpenId(), cookieCountView.getCount()));
            cookies.forEach(cookie -> {
                String openId = cookie.getOpenId();
                Long count = usage.get(openId);
                if (count == null) {
                    usage.put(openId, 0L);
                    queue.offer(cookie);
                } else if (count < 5L) {
                    queue.offer(cookie);
                } else {
                    usage.remove(openId);
                }
            });
            // 重设端点值
            endpoint.set(upper + 1);
        }
        logger.info("application={}, usage#size={}, queues#size={}", application, usage.size(), queue.size());
    }

    @Override
    public CookieDTO save(String cookieValue, ThirdPartyApplication application, long userId) throws IOException {
        CookieCheckDTO cookieCheckDTO = nodejsService.checkCookie(cookieValue, application);
        String openId = cookieCheckDTO.getOpenid();
        Cookie cookie = cookieRepository.findByOpenId(openId);
        if (cookie != null) {
            throw new BusinessException(ErrorCode.COOKIE_EXIST, "cookieValue={}, application={}, userId={}",
                    cookieValue, application, userId);
        }
        cookie = new Cookie();
        cookie.setValue(cookieValue);
        cookie.setService(HttpService.values()[cookieCheckDTO.getService()]);
        cookie.setApplication(application);
        cookie.setOpenId(openId);
        cookie.setNickname(Entities.encodeNickname(cookieCheckDTO.getNickname()));
        cookie.setHeadImgUrl(cookieCheckDTO.getHeadimgurl());
        cookie.setUserId(userId);
        cookie.setGmtCreate(Timestamp.from(Instant.now()));
        cookieRepository.save(cookie);
        CookieDTO cookieDTO = new CookieDTO();
        BeanUtils.copyProperties(cookie, cookieDTO);
        cookieDTO.setNickname(Entities.decodeNickname(cookie.getNickname()));
        return cookieDTO;
    }

    @Transactional
    @Override
    public void delete(long cookieId, long userId) {
        Cookie cookie = cookieRepository.findByIdAndUserId(cookieId, userId);
        if (cookie == null) {
            throw new BusinessException(ErrorCode.COOKIE_NOT_EXIST, "cookieId={}, userId={}, cookie={}", cookieId,
                    userId, cookie);
        }
        ThirdPartyApplication application = cookie.getApplication();
        if (application.equals(ThirdPartyApplication.MEITUAN)) {
            throw new BusinessException(ErrorCode.COOKIE_MEITUAN_DELETE_FAILURE,
                    "cookieId={}, application={}, userId={}", cookieId, application, userId);
        }
        Receiving receiving = receivingRepository.findByApplicationAndStatusAndUserId(application, ReceivingStatus.ING,
                userId);
        if (receiving != null) {
            throw new BusinessException(ErrorCode.COOKIE_DELETE_FAILURE,
                    "cookieId={}, status={},userId={}, receiving={}", cookieId, ReceivingStatus.ING, userId, receiving);
        }
        long available = userService.getAvailable(application, userId);
        if (available < 5) {
            throw new BusinessException(ErrorCode.COOKIE_DELETE_EXCEPTION,
                    "cookieId={}, application={}, userId={}, available={}", cookieId, application, userId, available);
        }
        cookieRepository.delete(cookie);
    }

}
