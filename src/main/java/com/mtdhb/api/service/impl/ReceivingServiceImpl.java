package com.mtdhb.api.service.impl;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mtdhb.api.constant.CookieStatus;
import com.mtdhb.api.constant.ErrorCode;
import com.mtdhb.api.constant.ReceivingStatus;
import com.mtdhb.api.constant.ThirdPartyApplication;
import com.mtdhb.api.dao.CookieCountRepository;
import com.mtdhb.api.dao.CookieMarkRepository;
import com.mtdhb.api.dao.ReceivingRepository;
import com.mtdhb.api.dto.ReceivingCarouselDTO;
import com.mtdhb.api.dto.ReceivingDTO;
import com.mtdhb.api.dto.ReceivingPieDTO;
import com.mtdhb.api.dto.ReceivingTrendDTO;
import com.mtdhb.api.dto.nodejs.CookieStatusDTO;
import com.mtdhb.api.dto.nodejs.RedPacketDTO;
import com.mtdhb.api.dto.nodejs.RedPacketResultDTO;
import com.mtdhb.api.dto.nodejs.ResultDTO;
import com.mtdhb.api.entity.Cookie;
import com.mtdhb.api.entity.CookieCount;
import com.mtdhb.api.entity.CookieMark;
import com.mtdhb.api.entity.Receiving;
import com.mtdhb.api.entity.view.ReceivingCarouselView;
import com.mtdhb.api.entity.view.ReceivingPieView;
import com.mtdhb.api.entity.view.ReceivingTrendView;
import com.mtdhb.api.exception.BusinessException;
import com.mtdhb.api.service.CookieService;
import com.mtdhb.api.service.NodejsService;
import com.mtdhb.api.service.ReceivingService;
import com.mtdhb.api.service.UserService;
import com.mtdhb.api.task.DispatchTask;
import com.mtdhb.api.task.ReceiveTask;
import com.mtdhb.api.util.Entities;

/**
 * @author i@huangdenghe.com
 * @date 2018/04/17
 */
@Service
public class ReceivingServiceImpl implements ReceivingService {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private CookieService cookieService;
    @Autowired
    private NodejsService nodejsService;
    @Autowired
    private UserService userService;
    @Autowired
    private CookieCountRepository cookieCountRepository;
    @Autowired
    private CookieMarkRepository cookieMarkRepository;
    @Autowired
    private ReceivingRepository receivingRepository;
    @Resource(name = "asynDispatchPools")
    private ThreadPoolExecutor[] asynDispatchPools;
    @Resource(name = "asynReceivePools")
    private ThreadPoolExecutor[] asynReceivePools;
    @Resource(name = "usage")
    private Map<String, Long> usage;
    @Resource(name = "queues")
    private List<LinkedBlockingQueue<Cookie>> queues;
    @Resource(name = "thresholds")
    private int[] thresholds;

    @Override
    public ReceivingDTO get(long receivingId, long userId) {
        Receiving receiving = receivingRepository.findByIdAndUserId(receivingId, userId);
        if (receiving == null) {
            throw new BusinessException(ErrorCode.RECEIVING_NOT_EXIST, "receivingId={}, userId={}, receving={}",
                    receivingId, userId, receiving);
        }
        ReceivingDTO receivingDTO = new ReceivingDTO();
        BeanUtils.copyProperties(receiving, receivingDTO);
        receivingDTO.setPhone(Entities.encodePhone(receivingDTO.getPhone()));
        return receivingDTO;
    }

    @Override
    public List<ReceivingDTO> list(long userId) {
        Slice<Receiving> reveivings = receivingRepository.findByUserId(userId,
                new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "id")));
        List<ReceivingDTO> receivingDTOs = reveivings.getContent().stream().map(reveiving -> {
            ReceivingDTO receivingDTO = new ReceivingDTO();
            BeanUtils.copyProperties(reveiving, receivingDTO);
            receivingDTO.setPhone(Entities.encodePhone(receivingDTO.getPhone()));
            return receivingDTO;
        }).collect(Collectors.toList());
        return receivingDTOs;
    }

    @Cacheable(cacheNames = "RECEIVING_CAROUSEL")
    @Override
    public List<ReceivingCarouselDTO> listReceivingCarousel() {
        Slice<ReceivingCarouselView> slice = receivingRepository
                .findReceivingCarouselView(new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "gmtModified")));
        List<ReceivingCarouselDTO> carouselReceivingDTOs = slice.map(receivingCarouselView -> {
            ReceivingCarouselDTO receivingCarouselDTO = new ReceivingCarouselDTO();
            String mail = receivingCarouselView.getMail();
            int index = mail.indexOf("@");
            String prefix = mail.substring(0, index);
            prefix = prefix.length() > 4 ? prefix.substring(0, 4) : prefix;
            mail = prefix + "****";
            BeanUtils.copyProperties(receivingCarouselView, receivingCarouselDTO);
            receivingCarouselDTO.setMail(mail);
            return receivingCarouselDTO;
        }).getContent();
        return carouselReceivingDTOs;
    }

    @Cacheable(cacheNames = "RECEIVING_TREND")
    @Override
    public List<ReceivingTrendDTO> listReceivingTrend(ThirdPartyApplication application) {
        Timestamp thisWeek = Timestamp
                .valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.ofSecondOfDay(0)).minusDays(6));
        List<ReceivingTrendView> receivingTrendViews = receivingRepository.findReceivingTrendView(application,
                thisWeek);
        List<ReceivingTrendDTO> receivingTrendDTOs = receivingTrendViews.stream().map(receivingTrendView -> {
            ReceivingTrendDTO receivingTrendDTO = new ReceivingTrendDTO();
            BeanUtils.copyProperties(receivingTrendView, receivingTrendDTO);
            return receivingTrendDTO;
        }).collect(Collectors.toList());
        return receivingTrendDTOs;
    }

    @Cacheable(cacheNames = "RECEIVING_PIE")
    @Override
    public List<ReceivingPieDTO> listReceivingPie(ThirdPartyApplication application) {
        List<ReceivingPieView> receivingPieViews = receivingRepository.findReceivingPieView(application);
        List<ReceivingPieDTO> receivingPieDTOs = receivingPieViews.stream().map(receivingPieView -> {
            ReceivingPieDTO receivingPieDTO = new ReceivingPieDTO();
            BeanUtils.copyProperties(receivingPieView, receivingPieDTO);
            return receivingPieDTO;
        }).collect(Collectors.toList());
        return receivingPieDTOs;
    }

    @Override
    public ReceivingDTO save(String key, String url, String phone, ThirdPartyApplication application, long userId) {
        checkReceiveTime();
        Receiving receiving = null;
        receiving = receivingRepository.findByUrlKeyAndApplicationAndStatusNot(key, application,
                ReceivingStatus.FAILURE);
        if (receiving != null) {
            throw new BusinessException(ErrorCode.RED_PACKET_EXIST, "key={}, application={}, status={}, receiving={}",
                    key, application, ReceivingStatus.FAILURE, receiving);
        }
        receiving = receivingRepository.findByApplicationAndStatusAndUserId(application, ReceivingStatus.ING, userId);
        if (receiving != null) {
            throw new BusinessException(ErrorCode.RECEIVE_WAIT, "application={}, status={}, userId={}, receiving={}",
                    application, ReceivingStatus.ING, userId, receiving);
        }
        long available = userService.getAvailable(application, userId);
        if (available < 2L) {
            throw new BusinessException(ErrorCode.AVAILABLE_INSUFFICIENT, "application={}, userId={}, available={}",
                    application, userId, available);
        }
        receiving = new Receiving();
        receiving.setUrlKey(key);
        receiving.setUrl(url);
        receiving.setPhone(phone);
        receiving.setApplication(application);
        receiving.setStatus(ReceivingStatus.ING);
        receiving.setUserId(userId);
        receiving.setGmtCreate(Timestamp.from(Instant.now()));
        receivingRepository.save(receiving);
        asynDispatch(receiving, available);
        ReceivingDTO receivingDTO = new ReceivingDTO();
        BeanUtils.copyProperties(receiving, receivingDTO);
        receivingDTO.setPhone(Entities.encodePhone(receivingDTO.getPhone()));
        return receivingDTO;
    }

    @Override
    public void asynDispatch(Receiving receiving, long available) {
        ThirdPartyApplication application = receiving.getApplication();
        DispatchTask dispatchTask = new DispatchTask(this, receiving, available);
        asynDispatchPools[application.ordinal()].execute(dispatchTask);
    }

    @Override
    public void dispatch(Receiving receiving, long available) {
        ThirdPartyApplication application = receiving.getApplication();
        LinkedBlockingQueue<Cookie> queue = queues.get(application.ordinal());
        int threshold = thresholds[application.ordinal()];
        int size = queue.size();
        logger.info("queue#size={}", size);
        if (size < threshold) {
            cookieService.load(application);
            size = queue.size();
        }
        if (size < 1) {
            saveFailedReceiving(receiving, ErrorCode.COOKIE_INSUFFICIENT.getMessage());
            return;
        }
        // TODO 配置
        size = Math.min(size, 30);
        List<Cookie> cookies = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Cookie cookie = queue.poll();
            if (cookie == null) {
                break;
            }
            cookies.add(cookie);
        }
        asynReceive(receiving, cookies, available);

    }

    @Override
    public void asynReceive(Receiving receiving, List<Cookie> cookies, long available) {
        ThirdPartyApplication application = receiving.getApplication();
        ReceiveTask receiveTask = new ReceiveTask(this, receiving, cookies, available);
        asynReceivePools[application.ordinal()].execute(receiveTask);
    }

    @Override
    public void receive(Receiving receiving, List<Cookie> cookies, long available) {
        logger.info("receiving={}", receiving);
        long userId = receiving.getUserId();
        String url = receiving.getUrl();
        String phone = receiving.getPhone();
        ThirdPartyApplication application = receiving.getApplication();
        LinkedBlockingQueue<Cookie> queue = queues.get(application.ordinal());
        try {
            ResultDTO<RedPacketDTO> resultDTO = nodejsService.getHongbao(url, phone, application, available, cookies);
            RedPacketDTO redPacketDTO = resultDTO.getData();
            if (redPacketDTO == null) {
                // TODO 现在暂时不限制领取，异常情况 cookie 直接放回队列
                cookies.stream().forEach(cookie -> queue.offer(cookie));
                saveFailedReceiving(receiving, resultDTO.getMessage());
                return;
            }
            // Cookie 的使用统计
            List<CookieStatusDTO> cookieStatusDTOs = redPacketDTO.getCookies();
            final AtomicInteger useCookieCount = new AtomicInteger();
            final Map<Long, Integer> cookieStatus = cookieStatusDTOs.stream()
                    .collect(Collectors.toMap(CookieStatusDTO::getId, CookieStatusDTO::getStatus));
            List<CookieCount> cookieCounts = new LinkedList<>();
            List<CookieMark> cookieMarks = new LinkedList<>();
            cookies.forEach(cookie -> {
                long cookieId = cookie.getId();
                String openId = cookie.getOpenId();
                Integer status = cookieStatus.get(cookieId);
                if (status == null) {
                    // 未使用的放回队列
                    queue.offer(cookie);
                } else if (status == CookieStatus.SUCCESS.ordinal()) {
                    // 已使用
                    useCookieCount.incrementAndGet();
                    long n = usage.remove(openId) + 1;
                    if (n < 5) {
                        usage.put(openId, n);
                        // 未达到5次领取则放回队列
                        queue.offer(cookie);
                    }
                    CookieCount count = new CookieCount();
                    count.setApplication(application);
                    count.setUserId(userId);
                    count.setCookieId(cookieId);
                    count.setOpenId(openId);
                    count.setReceivingId(receiving.getId());
                    count.setGmtCreate(Timestamp.from(Instant.now()));
                    cookieCounts.add(count);
                } else if (status == CookieStatus.INVALID.ordinal()) {
                    // 失效 cookie 标记，可能是美团、饿了么更新了规则
                    CookieMark cookieMark = new CookieMark();
                    cookieMark.setApplication(application);
                    cookieMark.setStatus(CookieStatus.INVALID);
                    cookieMark.setCookieId(cookieId);
                    cookieMark.setUserId(cookie.getUserId());
                    cookieMark.setGmtCreate(Timestamp.from(Instant.now()));
                    cookieMarks.add(cookieMark);
                } else if (status == CookieStatus.LIMIT.ordinal()) {
                    // 未达到5次领取就失效 cookie 处理
                    CookieMark cookieMark = new CookieMark();
                    cookieMark.setApplication(application);
                    cookieMark.setStatus(CookieStatus.LIMIT);
                    cookieMark.setCookieId(cookieId);
                    cookieMark.setUserId(cookie.getUserId());
                    cookieMark.setGmtCreate(Timestamp.from(Instant.now()));
                    cookieMarks.add(cookieMark);
                } else {
                    queue.offer(cookie);
                }
            });

            if (cookieCounts.size() > 0) {
                // TODO 可更优化为 mysql native 批量插入
                cookieCountRepository.save(cookieCounts);
                cookieMarkRepository.save(cookieMarks);
            }
            if (resultDTO.getCode() != 0) {
                saveFailedReceiving(receiving, resultDTO.getMessage());
                return;
            }
            if (useCookieCount.get() < 2) {
                RedPacketResultDTO redPacketResultDTO = redPacketDTO.getResult();
                receiving.setNickname(Entities.encodeNickname(redPacketResultDTO.getNickname()));
                receiving.setPrice(redPacketResultDTO.getPrice());
                receiving.setDate(redPacketResultDTO.getDate());
                saveFailedReceiving(receiving, resultDTO.getMessage());
                return;
            }
            RedPacketResultDTO redPacketResultDTO = redPacketDTO.getResult();
            receiving.setNickname(Entities.encodeNickname(redPacketResultDTO.getNickname()));
            receiving.setPrice(redPacketResultDTO.getPrice());
            receiving.setDate(redPacketResultDTO.getDate());
            receiving.setStatus(ReceivingStatus.SUCCESS);
            receiving.setGmtModified(Timestamp.from(Instant.now()));
            receivingRepository.save(receiving);
        } catch (IOException e) {
            logger.error("receiving={}, cookies={}", receiving, cookies, e);
            // TODO 现在暂时不限制领取，IO 异常 cookie 直接放回队列
            cookies.stream().forEach(cookie -> {
                queue.offer(cookie);
            });
            saveFailedReceiving(receiving, e.getMessage());
        } catch (Exception e) {
            logger.error("receiving={}, cookies={}", receiving, cookies, e);
            saveFailedReceiving(receiving, e.getMessage());
        }
    }

    private long checkReceiveTime() {
        // 23:50至00:10限制领取，防止当前服务时间已达00:00，但是美团或饿了么的服务器的时间还未到00:00，导致 cookie 使用统计出错
        Instant now = Instant.now();
        long nowEpochMilli = now.toEpochMilli();
        LocalDateTime today = LocalDateTime.of(LocalDate.now(), LocalTime.ofSecondOfDay(0));
        LocalDateTime tomorrow = today.plusDays(1);
        ZoneOffset defaultZoneOffset = ZoneOffset.ofHours(8);
        logger.info("now={}, today={}, tomorrow={}, defaultZoneOffset={}", now, today, tomorrow, defaultZoneOffset);
        long todayEpochMilli = today.toInstant(defaultZoneOffset).toEpochMilli();
        long tomorrowEpochMilli = tomorrow.toInstant(defaultZoneOffset).toEpochMilli();
        // TODO 配置
        long tenMinuteEpochMilli = 1000 * 60 * 10;
        if (tomorrowEpochMilli - nowEpochMilli < tenMinuteEpochMilli
                || nowEpochMilli - todayEpochMilli < tenMinuteEpochMilli) {
            throw new BusinessException(ErrorCode.SYSTEM_MAINTENANCE, "now={}, today={}, tomorrow={}", now, today,
                    tomorrow);
        }
        return todayEpochMilli;
    }

    private void saveFailedReceiving(Receiving receiving, String message) {
        message = message.length() > 512 ? message.substring(0, 512) : message;
        receiving.setMessage(message);
        receiving.setStatus(ReceivingStatus.FAILURE);
        receiving.setGmtModified(Timestamp.from(Instant.now()));
        receivingRepository.save(receiving);
    }

}
