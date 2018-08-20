package com.mtdhb.api.autoconfigure;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.mtdhb.api.util.IOStreams;

import lombok.extern.slf4j.Slf4j;

/**
 * @author i@huangdenghe.com
 * @date 2018/07/20
 */
@Configuration
@EnableConfigurationProperties(MailProperties.class)
@Slf4j
public class MailAutoConfiguration {

    private final MailProperties properties;

    MailAutoConfiguration(MailProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void readDefaultMailTemplate() {
        if (properties.getRegisterMailTemplate() == null) {
            properties.setRegisterMailTemplate(read(properties.getRegisterMailTemplatePath()));
        }
        if (properties.getResetPasswordMailTemplate() == null) {
            properties.setResetPasswordMailTemplate(read(properties.getResetPasswordMailTemplatePath()));
        }
    }

    private String read(String path) {
        String template = null;
        try (InputStream in = MailAutoConfiguration.class.getClassLoader().getResourceAsStream(path)) {
            template = new String(IOStreams.readAllBytes(in), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("path={}", path, e);
        }
        return template;
    }

}
