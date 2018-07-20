package com.mtdhb.api.autoconfigure;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.mtdhb.api.util.IOStreams;

/**
 * @author i@huangdenghe.com
 * @date 2018/07/20
 */
@Configuration
@EnableConfigurationProperties(MailProperties.class)
public class MailAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final MailProperties properties;

    MailAutoConfiguration(MailProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void readDefaultMailTemplate() {
        if (this.properties.getRegisterMailTemplate() == null) {
            this.properties.setRegisterMailTemplate(read(this.properties.getRegisterMailTemplatePath()));
        }
        if (this.properties.getResetPasswordMailTemplate() == null) {
            this.properties.setResetPasswordMailTemplate(read(this.properties.getResetPasswordMailTemplatePath()));
        }
    }

    private String read(String path) {
        String template = null;
        try (InputStream in = MailAutoConfiguration.class.getClassLoader().getResourceAsStream(path)) {
            template = new String(IOStreams.readAllBytes(in), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("path={}", path, e);
        }
        return template;
    }

}
