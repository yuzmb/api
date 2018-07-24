package com.mtdhb.api.task;

import com.mtdhb.api.util.Mail;

/**
 * @author i@huangdenghe.com
 * @date 2018/03/26
 */
public class SendMailTask implements Runnable {

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
        Mail.getInstance().send(to, subject, content);
    }

}
