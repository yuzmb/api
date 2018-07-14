package com.mtdhb.api.task;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mtdhb.api.util.Mail;

/**
 * @author i@huangdenghe.com
 * @date 2018/03/26
 */
public class SendMailTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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
        logger.info("SendMailTask#run starting...");
        Mail.getInstance().send(to, subject, content);
        logger.info("SendMailTask#run end");
    }

}
