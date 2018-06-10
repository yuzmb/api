package com.mtdhb.api.web.home;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mtdhb.api.constant.ThirdPartyApplication;
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
        UserDTO userDTO = RequestContextHolder.get();
        return Results.success(Stream.of(ThirdPartyApplication.values())
                .collect(Collectors.toMap(application -> application.name().toLowerCase(),
                        application -> cookieService.listCookieRank(application, userDTO.getId()))));
    }

    @RequestMapping("/trend")
    public Result trend() {
        return Results.success(Stream.of(ThirdPartyApplication.values())
                .collect(Collectors.toMap(application -> application.name().toLowerCase(),
                        application -> receivingService.listReceivingTrend(application))));
    }

    @RequestMapping("/pie")
    public Result pie() {
        return Results.success(Stream.of(ThirdPartyApplication.values())
                .collect(Collectors.toMap(application -> application.name().toLowerCase(),
                        application -> receivingService.listReceivingPie(application))));
    }

}
