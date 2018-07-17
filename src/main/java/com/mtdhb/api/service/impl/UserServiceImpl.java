package com.mtdhb.api.service.impl;

import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.mtdhb.api.configuration.MailConfiguration;
import com.mtdhb.api.constant.CacheNames;
import com.mtdhb.api.constant.e.ErrorCode;
import com.mtdhb.api.constant.e.Purpose;
import com.mtdhb.api.constant.e.ThirdPartyApplication;
import com.mtdhb.api.constant.e.VerificationType;
import com.mtdhb.api.dao.CookieCountRepository;
import com.mtdhb.api.dao.CookieRepository;
import com.mtdhb.api.dao.UserRepository;
import com.mtdhb.api.dao.VerificationRepository;
import com.mtdhb.api.dto.AccountDTO;
import com.mtdhb.api.dto.NumberDTO;
import com.mtdhb.api.dto.UserDTO;
import com.mtdhb.api.entity.User;
import com.mtdhb.api.entity.Verification;
import com.mtdhb.api.exception.BusinessException;
import com.mtdhb.api.service.AsyncService;
import com.mtdhb.api.service.UserService;
import com.mtdhb.api.util.Entities;
import com.mtdhb.api.util.SecureRandoms;

/**
 * @author i@huangdenghe.com
 * @date 2017/12/04
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private CookieRepository cookieRepository;
    @Autowired
    private CookieCountRepository cookieCountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationRepository verificationRepository;
    @Autowired
    private MailConfiguration mailConfiguration;

    @Override
    public UserDTO loginByMail(String mail, String password) {
        User user = userRepository.findByMail(mail);
        if (user == null) {
            throw new BusinessException(ErrorCode.ACCOUNT_OR_PASSWORD_ERROR, "mail={}, user={}", mail, user);
        }
        password = Entities.digestUserPassword(password, user.getSalt());
        if (!password.equals(user.getPassword())) {
            throw new BusinessException(ErrorCode.ACCOUNT_OR_PASSWORD_ERROR, "mail={}, password={}, user={}", mail,
                    password, user);
        }
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    @Override
    public UserDTO registerByMail(AccountDTO accountDTO) {
        Instant now = Instant.now();
        Timestamp timestamp = Timestamp.from(now);
        Timestamp before = Timestamp
                .from(now.minus(Duration.ofMinutes(mailConfiguration.getRegisterMailEffectiveTime())));
        logger.info("before={}", before);
        Verification verification = verificationRepository.findByCodeAndTypeAndPurposeAndUsedAndGmtCreateGreaterThan(
                accountDTO.getVerificationCode(), VerificationType.MAIL, Purpose.REGISTER, false, before);
        if (verification == null) {
            throw new BusinessException(ErrorCode.MAIL_VERIFICATION_EXCEPTION, "accountDTO={}, verification={}",
                    accountDTO, verification);
        }
        verification.setUsed(true);
        verification.setGmtModified(timestamp);
        verificationRepository.save(verification);
        User user = new User();
        user.setMail(verification.getObject());
        String salt = SecureRandoms.nextHex();
        user.setSalt(salt);
        user.setPassword(Entities.digestUserPassword(accountDTO.getPassword(), salt));
        user.setGmtCreate(timestamp);
        user.setToken(Entities.generateUserToken());
        user.setLocked(false);
        userRepository.save(user);
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    @Override
    public void sendRegisterMail(String mail) {
        String lowerCase = mail.toLowerCase();
        List<String> blacklist = mailConfiguration.getBlacklist();
        if (blacklist != null && blacklist.stream().anyMatch(domain -> lowerCase.endsWith(domain))) {
            throw new BusinessException(ErrorCode.MAIL_ON_BLACKLIST, "mail={}", mail);
        }
        User user = userRepository.findByMail(mail);
        if (user != null) {
            throw new BusinessException(ErrorCode.MAIL_EXIST, "mail={}, user{}", mail, user);
        }
        sendMail(mail, Purpose.REGISTER, mailConfiguration.getRegisterMailSubject(),
                mailConfiguration.getRegisterMailTemplate());
    }

    @Override
    public UserDTO resetPassword(AccountDTO accountDTO) {
        Instant now = Instant.now();
        Timestamp timestamp = Timestamp.from(now);
        Timestamp before = Timestamp
                .from(now.minus(Duration.ofMinutes(mailConfiguration.getResetPasswordMailEffectiveTime())));
        logger.info("before={}", before);
        Verification verification = verificationRepository.findByCodeAndTypeAndPurposeAndUsedAndGmtCreateGreaterThan(
                accountDTO.getVerificationCode(), VerificationType.MAIL, Purpose.RESET_PASSWORD, false, before);
        if (verification == null) {
            throw new BusinessException(ErrorCode.MAIL_VERIFICATION_EXCEPTION, "accountDTO={}, verification={}",
                    accountDTO, verification);
        }
        verification.setUsed(true);
        verification.setGmtModified(timestamp);
        verificationRepository.save(verification);
        User user = userRepository.findByMail(verification.getObject());
        String token = user.getToken();
        user.setPassword(Entities.digestUserPassword(accountDTO.getPassword(), user.getSalt()));
        user.setToken(Entities.generateUserToken());
        user.setGmtModified(timestamp);
        userRepository.save(user);
        // 使该用户旧 token 失效
        cacheManager.getCache(CacheNames.USER).evict(token);
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    @Override
    public void sendResetPasswordMail(String mail) {
        User user = userRepository.findByMail(mail);
        if (user == null) {
            throw new BusinessException(ErrorCode.MAIL_NOT_EXIST, "mail={}, user={}", mail, user);
        }
        sendMail(mail, Purpose.RESET_PASSWORD, mailConfiguration.getResetPasswordMailSubject(),
                mailConfiguration.getResetPasswordMailTemplate());
    }

    @Cacheable(cacheNames = CacheNames.USER)
    @Override
    public UserDTO getByToken(String token) {
        User user = userRepository.findByToken(token);
        if (user == null) {
            return null;
        }
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    @Override
    public long getAvailable(ThirdPartyApplication application, long userId) {
        return getNumber(application, userId).getAvailable();
    }

    @Override
    public NumberDTO getNumber(ThirdPartyApplication application, long userId) {
        long total = cookieRepository.countByApplicationAndUserId(application, userId) * 5;
        long used = cookieCountRepository.countByApplicationAndUserIdAndGmtCreateGreaterThan(application, userId,
                Timestamp.valueOf(LocalDate.now().atStartOfDay()));
        NumberDTO numberDTO = new NumberDTO();
        numberDTO.setAvailable(total - used);
        numberDTO.setTotal(total);
        return numberDTO;
    }

    private void sendMail(String mail, Purpose purpose, String subject, String template) {
        String code = Entities.generateVerificationCode();
        Verification verification = new Verification();
        verification.setObject(mail);
        verification.setType(VerificationType.MAIL);
        verification.setCode(code);
        verification.setPurpose(purpose);
        verification.setUsed(false);
        verification.setGmtCreate(Timestamp.from(Instant.now()));
        verificationRepository.save(verification);
        asyncService.sendMail(mail, subject, String.format(template, code, code));
    }

}
