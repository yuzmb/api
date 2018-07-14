package com.mtdhb.api.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.mtdhb.api.service.impl.UserServiceImpl;
import com.mtdhb.api.util.IOStreams;

import lombok.Data;

/**
 * @author i@huangdenghe.com
 * @date 2018/03/25
 */
@Data
@Component
@ConfigurationProperties(prefix = "com.mtdhb.api.mail")
public class MailConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * 注册邮件模板路径
     */
    private String registerMailTemplatePath = "registerMailTemplate.html";
    /**
     * 注册邮件模板
     */
    private String registerMailTemplate;
    /**
     * 注册邮件主题
     */
    private String registerMailSubject;
    /**
     * 注册邮件有效时间
     */
    private int registerMailEffectiveTime;
    /**
     * 重置密码模板路径
     */
    private String resetPasswordMailTemplatePath = "resetPasswordMailTemplate.html";
    /**
     * 重置密码邮件模板
     */
    private String resetPasswordMailTemplate;
    /**
     * 重置密码邮件主题
     */
    private String resetPasswordMailSubject;
    /**
     * 重置密码邮件有效时间
     */
    private int resetPasswordMailEffectiveTime;
    /**
     * 黑名单
     */
    private List<String> blacklist;

    @PostConstruct
    public void init() {
        if (registerMailTemplate == null) {
            registerMailTemplate = read(registerMailTemplatePath);
        }
        if (resetPasswordMailTemplate == null) {
            resetPasswordMailTemplate = read(resetPasswordMailTemplatePath);
        }
    }

    private String read(String path) {
        String template = null;
        try (InputStream in = UserServiceImpl.class.getClassLoader().getResourceAsStream(path)) {
            template = new String(IOStreams.readAllBytes(in), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("path={}", path, e);
        }
        return template;
    }

}
