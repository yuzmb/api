package com.mtdhb.api.web.home;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mtdhb.api.constant.SessionKeys;
import com.mtdhb.api.constant.e.ErrorCode;
import com.mtdhb.api.constant.e.ThirdPartyApplication;
import com.mtdhb.api.dto.AccountDTO;
import com.mtdhb.api.dto.MailDTO;
import com.mtdhb.api.dto.ReceivingDTO;
import com.mtdhb.api.dto.Result;
import com.mtdhb.api.dto.UserDTO;
import com.mtdhb.api.exception.BusinessException;
import com.mtdhb.api.service.CookieService;
import com.mtdhb.api.service.ReceivingService;
import com.mtdhb.api.service.UserService;
import com.mtdhb.api.util.Captcha;
import com.mtdhb.api.util.Connections;
import com.mtdhb.api.util.Results;
import com.mtdhb.api.util.Synchronizes;
import com.mtdhb.api.web.RequestContextHolder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author i@huangdenghe.com
 * @date 2017/12/02
 */
@RequestMapping("/user")
@RestController
@Slf4j
public class UserController {

    private static final Pattern PATTERN = Pattern.compile("^Cookie:.+", Pattern.CASE_INSENSITIVE);

    @Autowired
    private UserService userService;
    @Autowired
    private CookieService cookieService;
    @Autowired
    private ReceivingService receivingService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result login(@RequestParam("account") String account, @RequestParam("password") String password) {
        // TODO 暂时只支持电子邮件登录
        UserDTO userDTO = userService.loginByMail(account, password);
        return Results.success(userDTO);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Result register(@Valid AccountDTO accountDTO) {
        // TODO 暂时只支持电子邮件注册
        UserDTO userDTO = userService.registerByMail(accountDTO);
        if (userDTO.getLocked()) {
            throw new BusinessException(ErrorCode.USER_LOCKED, "userDTO={}", userDTO);
        }
        return Results.success(userDTO);
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public Result resetPassword(@Valid AccountDTO accountDTO) {
        userService.resetPassword(accountDTO);
        return Results.success(true);
    }

    @Deprecated
    @RequestMapping("/logout")
    public Result logout(HttpServletResponse response) {
        return Results.success(true);
    }

    @RequestMapping(value = "/registerMail", method = RequestMethod.POST)
    public Result registerMail(@Valid MailDTO mailDTO, HttpSession session) {
        checkCaptcha(mailDTO.getCaptcha(), SessionKeys.REGISTER_CAPTCHA, session);
        userService.sendRegisterMail(mailDTO.getMail());
        return Results.success(true);
    }

    @RequestMapping("/registerCaptcha")
    public void registerCaptcha(HttpSession session, HttpServletResponse response) throws IOException {
        writeCaptcha(SessionKeys.REGISTER_CAPTCHA, session, response);
    }

    @RequestMapping(value = "/resetPasswordMail", method = RequestMethod.POST)
    public Result resetPasswordMail(@Valid MailDTO mailDTO, HttpSession session) {
        checkCaptcha(mailDTO.getCaptcha(), SessionKeys.RESET_PASSWORD_CAPTCHA, session);
        userService.sendResetPasswordMail(mailDTO.getMail());
        return Results.success(true);
    }

    @RequestMapping("/resetPasswordCaptcha")
    public void resetPasswordCaptcha(HttpSession session, HttpServletResponse response) throws IOException {
        writeCaptcha(SessionKeys.RESET_PASSWORD_CAPTCHA, session, response);
    }

    @RequestMapping(value = "/cookie", method = RequestMethod.POST)
    public Result cookie(@RequestParam("value") String value, @RequestParam("application") int application)
            throws IOException {
        UserDTO userDTO = RequestContextHolder.get();
        long userId = userDTO.getId();
        // 去除首尾空白字符
        value = value.trim();
        // 处理兼容处理带 Cookie: 前缀的提交
        if (PATTERN.matcher(value).matches()) {
            value = value.substring("Cookie:".length()).trim();
        }
        ThirdPartyApplication[] applications = ThirdPartyApplication.values();
        if (application < 0 || application >= applications.length) {
            throw new BusinessException(ErrorCode.THIRDPARTYAPPLICATION_EXCEPTION, "application={}", application);
        }
        return Results.success(cookieService.save(value, applications[application], userId));
    }

    @RequestMapping(value = "/cookie", method = RequestMethod.GET)
    public Result cookie() {
        UserDTO userDTO = RequestContextHolder.get();
        long userId = userDTO.getId();
        return Results.success(cookieService.list(userId));
    }

    @RequestMapping(value = "/cookie/{cookieId}", method = RequestMethod.DELETE)
    public Result cookie(@PathVariable("cookieId") long cookieId) {
        UserDTO userDTO = RequestContextHolder.get();
        long userId = userDTO.getId();
        cookieService.delete(cookieId, userId);
        return Results.success(true);
    }

    @RequestMapping(value = "/receiving", method = RequestMethod.POST)
    public Result receiving(@RequestParam("url") String url, @RequestParam("phone") String phone) {
        UserDTO userDTO = RequestContextHolder.get();
        long userId = userDTO.getId();
        url = url.trim();
        String urlKey = null;
        ThirdPartyApplication application = null;
        ReceivingDTO receivingDTO = null;
        // 某些地方复制出的链接带 &amp; 而不是 &
        url = url.replace("&amp;", "&");
        // 很多用户用手机复制链接的时候会带上末尾的 ]
        if (url.endsWith("]")) {
            url = url.substring(0, url.length() - 1);
        }
        URL spec = null;
        try {
            // 支持 url.cn 的短链接
            if (url.startsWith("https://url.cn/") || url.startsWith("http://url.cn/")) {
                url = Connections.getRedirectURL(url);
            }
            spec = new URL(url);
        } catch (Exception e) {
            log.warn("url={}", url, e);
            throw new BusinessException(ErrorCode.URL_ERROR, "url={}", url);
        }
        if (url.startsWith("https://h5.ele.me/hongbao/")) {
            urlKey = getParmeter(spec.getRef(), "sn");
            application = ThirdPartyApplication.ELE;
        } else if (url.startsWith("https://activity.waimai.meituan.com/")
                || url.startsWith("http://activity.waimai.meituan.com/")) {
            urlKey = getParmeter(spec.getQuery(), "urlKey");
            application = ThirdPartyApplication.MEITUAN;
        }
        if (urlKey == null) {
            throw new BusinessException(ErrorCode.URL_ERROR, "url={}", url);
        }
        String receivingLock = Synchronizes.buildReceivingLock(urlKey, application);
        String userReceiveLock = Synchronizes.buildUserReceiveLock(application, userId);
        synchronized (receivingLock) {
            synchronized (userReceiveLock) {
                receivingDTO = receivingService.save(urlKey, url, phone, application, userId);
            }
        }
        return Results.success(receivingDTO);
    }

    @RequestMapping(value = "/receiving", method = RequestMethod.GET)
    public Result receiving() {
        UserDTO userDTO = RequestContextHolder.get();
        long userId = userDTO.getId();
        List<ReceivingDTO> receivingDTOs = receivingService.list(userId);
        return Results.success(receivingDTOs);
    }

    @RequestMapping(value = "/receiving/{receivingId}", method = RequestMethod.GET)
    public Result receiving(@PathVariable("receivingId") long receivingId) {
        UserDTO userDTO = RequestContextHolder.get();
        long userId = userDTO.getId();
        ReceivingDTO receivingDTO = receivingService.get(receivingId, userId);
        return Results.success(receivingDTO);
    }

    @RequestMapping(value = "/number")
    public Result number() {
        UserDTO userDTO = RequestContextHolder.get();
        return Results.success(Stream.of(ThirdPartyApplication.values())
                .collect(Collectors.toMap(application -> application.name().toLowerCase(),
                        application -> userService.getNumber(application, userDTO.getId()))));
    }

    private String getParmeter(String query, String name) {
        Optional<String> optional = Stream.of(query.split("&")).filter(keyValue -> keyValue.trim().startsWith(name))
                .map(keyValue -> keyValue.split("=")[1]).findFirst();
        return optional.orElse(null);
    }

    private void checkCaptcha(String captcha, String sessionKey, HttpSession session) {
        String sessionCaptcha = (String) session.getAttribute(sessionKey);
        session.removeAttribute(sessionKey);
        log.info("{} captcha={}, sessionCaptcha={}", sessionKey, captcha, sessionCaptcha);
        if (!captcha.equalsIgnoreCase(sessionCaptcha)) {
            throw new BusinessException(ErrorCode.CAPTCHA_ERROR, "{} captcha={}, sessionCaptcha={}", sessionKey,
                    captcha, sessionCaptcha);
        }
    }

    private void writeCaptcha(String sessionKey, HttpSession session, HttpServletResponse response) throws IOException {
        Captcha captcha = new Captcha();
        String code = captcha.getCode();
        log.info("{} captcha={}", sessionKey, code);
        session.setAttribute(sessionKey, code);
        response.setDateHeader(HttpHeaders.EXPIRES, -1);
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
        response.setHeader(HttpHeaders.PRAGMA, "no-cache");
        ImageIO.write(captcha.getImage(), "JPEG", response.getOutputStream());
    }

}
