package com.mtdhb.api.autoconfigure;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author i@huangdenghe.com
 * @date 2018/07/20
 */
@ConfigurationProperties(prefix = "com.mtdhb.api.mail")
@Data
public class MailProperties {

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

}
