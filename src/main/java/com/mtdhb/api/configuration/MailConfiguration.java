package com.mtdhb.api.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;

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

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * 注册邮件模板路径
     */
    private final static String REGISTER_MAIL_TEMPLATE_PATH = "registerMailTemplate.html";
    /**
     * 重置密码模板路径
     */
    private final static String RESET_PASSWORD_MAILTEMPLATE_PATH = "resetPasswordMailTemplate.html";

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

    @PostConstruct
    public void init() {
        try (InputStream registerMailTemplateInputStream = UserServiceImpl.class.getClassLoader()
                .getResourceAsStream(REGISTER_MAIL_TEMPLATE_PATH);
                InputStream resetPasswordMailTemplateInputStream = UserServiceImpl.class.getClassLoader()
                        .getResourceAsStream(RESET_PASSWORD_MAILTEMPLATE_PATH)) {
            registerMailTemplate = new String(IOStreams.readAllBytes(registerMailTemplateInputStream),
                    StandardCharsets.UTF_8);
            resetPasswordMailTemplate = new String(IOStreams.readAllBytes(resetPasswordMailTemplateInputStream),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("mailConfiguration={}", this, e);
        }
    }

}
