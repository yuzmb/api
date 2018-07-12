package com.mtdhb.api.web.home;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mtdhb.api.constant.e.ThirdPartyApplication;
import com.mtdhb.api.dto.Result;
import com.mtdhb.api.dto.UserDTO;
import com.mtdhb.api.service.CookieService;
import com.mtdhb.api.service.ReceivingService;
import com.mtdhb.api.util.Results;
import com.mtdhb.api.web.RequestContextHolder;

/**
 * @author i@huangdenghe.com
 * @date 2017/11/30
 */
@RestController
public class HomeController {

    @Autowired
    private CookieService cookieService;
    @Autowired
    private ReceivingService receivingService;

    private ChronoField[] chronoFields = { ChronoField.DAY_OF_WEEK, ChronoField.DAY_OF_MONTH, ChronoField.DAY_OF_YEAR };

    @RequestMapping("/user")
    public Result user(HttpSession session) {
        UserDTO userDTO = RequestContextHolder.get();
        // 只在注册和登录接口返回 token
        userDTO.setToken(null);
        return Results.success(userDTO);
    }

    @RequestMapping("/zhuangbi")
    public Result carousel() {
        return Results.success(receivingService.listReceivingCarousel());
    }

    @RequestMapping("/rank")
    public Result rank() {
        return Results.success(Stream.of(ThirdPartyApplication.values())
                .collect(Collectors.toMap(application -> application.name().toLowerCase(),
                        application -> cookieService.listCookieRank(application))));
    }

    @RequestMapping("/trend")
    public Result trend() {
        return Results.success(Stream.of(ThirdPartyApplication.values())
                .collect(Collectors.toMap(application -> application.name().toLowerCase(),
                        application -> receivingService.listReceivingTrend(application))));
    }

    @RequestMapping("/pie")
    public Result pie(@RequestParam(value = "type", required = false, defaultValue = "-1") int type) {
        LocalDate localDate = LocalDate.now();
        if (type > 0 && type < chronoFields.length) {
            localDate = localDate.with(chronoFields[type], 1);
        }
        Timestamp timestamp = Timestamp.valueOf(localDate.atStartOfDay());
        return Results.success(Stream.of(ThirdPartyApplication.values())
                .collect(Collectors.toMap(application -> application.name().toLowerCase(),
                        application -> receivingService.listReceivingPie(application, timestamp))));
    }

}
