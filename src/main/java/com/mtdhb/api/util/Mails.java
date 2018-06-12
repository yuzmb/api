package com.mtdhb.api.util;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO 由于免费企业邮箱有发送数量和频率的限制，不足以支撑我们网站的发件量, 而我们又买不起收费的，曾导致大量注册我们网站的用户收不到注册邮件。
 * 所以我们采取配置多个免费企业邮箱解决方式。目前配置较为麻烦，有待优化
 * 
 * @author i@huangdenghe.com
 * @date 2018/03/17
 */
public class Mails {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * 企业邮箱配置的数量
     */
    private static final int SIZE = 4;
    private static final Properties[] PROPERTIES = new Properties[SIZE];
    private static final AtomicInteger INTEGER = new AtomicInteger();

    static {
        for (int i = 0; i < SIZE; i++) {
            try (Reader reader = new InputStreamReader(
                    Mails.class.getClassLoader().getResourceAsStream("mail" + i + ".properties"),
                    StandardCharsets.UTF_8)) {
                PROPERTIES[i] = new Properties();
                PROPERTIES[i].load(reader);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * @param to
     *            收件人，多个收件人用;分隔
     * @param cc
     *            抄送，多个抄送用;分隔
     * @param bcc
     *            密送，多个密送用;分隔
     * @param subject
     *            主题
     * @param html
     *            内容，引用内嵌资源（图片）示例：<img src="cid:图片的文件名" />
     * @param images
     *            内嵌资源（图片）
     * @param attachments
     *            附件
     * @throws UnsupportedEncodingException
     * @throws MessagingException
     */
    public static void send(String to, String cc, String bcc, String subject, String html, File[] images,
            File[] attachments) throws UnsupportedEncodingException, MessagingException {
        int index = INTEGER.updateAndGet(current -> current + 1 < SIZE ? current + 1 : 0);
        // int index = 2;
        Properties properties = PROPERTIES[index];
        // TODO key 要声明成常量
        String personal = properties.getProperty("com.mtdhb.mail.personal");
        String user = properties.getProperty("com.mtdhb.mail.user");
        String password = properties.getProperty("com.mtdhb.mail.password");
        logger.info("index={}, personal={}, user={}, password={}", index, personal, user, password);
        // 此处要用 Session#getInstance，Session#getDefaultInstance 为单例
        Session session = Session.getInstance(properties);
        MimeMessage message = new MimeMessage(session);
        // 设置发件人
        message.setFrom(new InternetAddress(user, MimeUtility.encodeText(personal)));
        // 添加收件人
        addRecipients(message, Message.RecipientType.TO, to);
        // 添加抄送
        if (cc != null) {
            addRecipients(message, Message.RecipientType.CC, cc);
        }
        // 添加密送
        if (bcc != null) {
            addRecipients(message, Message.RecipientType.BCC, bcc);
        }
        // 设置主题
        message.setSubject(subject);
        // 最外层部分
        MimeMultipart wrapPart = new MimeMultipart();
        MimeMultipart htmlWithImageMultipart = new MimeMultipart();
        // 文本部分
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(html, "text/html; charset=UTF-8");
        htmlWithImageMultipart.addBodyPart(htmlPart);
        // 内嵌资源（图片）部分
        if (images != null) {
            for (int i = 0; i < images.length; i++) {
                MimeBodyPart imagePart = new MimeBodyPart();
                DataHandler dataHandler = new DataHandler(new FileDataSource(images[i]));
                imagePart.setDataHandler(dataHandler);
                imagePart.setContentID(images[i].getName());
                htmlWithImageMultipart.addBodyPart(imagePart);
            }
            htmlWithImageMultipart.setSubType("related");
        }
        MimeBodyPart htmlWithImageBodyPart = new MimeBodyPart();
        htmlWithImageBodyPart.setContent(htmlWithImageMultipart);
        wrapPart.addBodyPart(htmlWithImageBodyPart);
        // 附件部分
        if (attachments != null) {
            for (int i = 0; i < attachments.length; i++) {
                MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                DataHandler dataHandler = new DataHandler(new FileDataSource(attachments[i]));
                // 获得文件名，用户手工指定文件名
                String fileName = dataHandler.getName();
                attachmentBodyPart.setDataHandler(dataHandler);
                // 显示指定文件名（防止文件名乱码）
                attachmentBodyPart.setFileName(MimeUtility.encodeText(fileName));
                wrapPart.addBodyPart(attachmentBodyPart);
            }
            wrapPart.setSubType("mixed");
        }
        message.setContent(wrapPart);
        message.saveChanges();
        // 实例方法发送
        Transport transport = session.getTransport();
        transport.connect(user, password);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }

    /**
     * @param to
     *            收件人，多个收件人用;分隔
     * @param subject
     *            主题
     * @param html
     *            内容
     * @throws Exception
     */
    public static void send(String to, String subject, String html)
            throws UnsupportedEncodingException, MessagingException {
        send(to, null, null, subject, html, null, null);
    }

    /**
     * @param to
     *            收件人，多个收件人用;分隔
     * @param subject
     *            主题
     * @param html
     *            内容
     * @param attachments
     *            附件
     * @throws UnsupportedEncodingException
     * @throws MessagingException
     */
    public static void send(String to, String subject, String html, File[] attachments)
            throws UnsupportedEncodingException, MessagingException {
        send(to, null, null, subject, html, null, attachments);
    }

    private static void addRecipients(MimeMessage message, RecipientType type, String recipients)
            throws MessagingException {
        String[] addresses = recipients.split(";");
        for (int i = 0; i < addresses.length; i++) {
            message.addRecipients(type, addresses[i]);
        }
    }

}