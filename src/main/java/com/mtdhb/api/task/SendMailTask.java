package com.mtdhb.api.task;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mtdhb.api.util.Mails;

/**
 * @author i@huangdenghe.com
 * @date 2018/03/26
 */
public class SendMailTask implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private String to;
    private String subject;
    private String content;

    public SendMailTask(String to, String subject, String content) {
        super();
        this.to = to;
        this.subject = subject;
        this.content = content;
    }

    @Override
    public void run() {
        logger.info("SendMailTask: to={}, subject={}, content={}", to, subject, content);
        try {
            Mails.send(to, subject, content);
        } catch (Exception e) {
            logger.error("SendMailTask: to={}, subject={}, content={}", to, subject, content, e);
        }
    }

}
