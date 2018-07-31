package com.mtdhb.api.util;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

import lombok.extern.slf4j.Slf4j;

/**
 * 支持多邮箱配置的邮件发送类
 * 
 * <p>由于免费企业邮箱有发送数量和频率的限制，不足以支撑我们网站的邮件发送； 而我们 又买不起收费的，所以我们采用配置多个免费企业邮箱方式解决
 * 
 * @author i@huangdenghe.com
 * @date 2018/03/17
 */
@Slf4j
public class Mail {

    private ArrayList<Properties> propertiesList = new ArrayList<>();
    private ArrayList<Session> sessionList = new ArrayList<>();
    private AtomicInteger position = new AtomicInteger();
    private int size;

    private Mail() {
        for (int i = 0; true; i++) {
            InputStream in = Mail.class.getClassLoader().getResourceAsStream("mail" + i + ".properties");
            if (in == null) {
                break;
            }
            log.info("i={}", i);
            try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);) {
                Properties properties = new Properties();
                properties.load(reader);
                // 此处要用 Session#getInstance，Session#getDefaultInstance 为单例
                Session session = Session.getInstance(properties);
                propertiesList.add(properties);
                sessionList.add(session);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                break;
            }
        }
        size = propertiesList.size();
        log.info("size={}", size);
    }

    public static Mail getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * 发送邮件
     * 
     * @param to
     *            收件人，多个收件人用 {@code ;} 分隔
     * @param subject
     *            主题
     * @param content
     *            内容
     * @return 如果邮件发送成功，则返回 {@code true}，否则返回 {@code false}
     */
    public boolean send(String to, String subject, String content) {
        return send(to, null, null, subject, content, null, null);
    }

    /**
     * 发送邮件
     * 
     * @param to
     *            收件人，多个收件人用 {@code ;} 分隔
     * @param cc
     *            抄送，多个抄送用 {@code ;} 分隔
     * @param bcc
     *            密送，多个密送用 {@code ;} 分隔
     * @param subject
     *            主题
     * @param content
     *            内容，可引用内嵌图片，引用方式：{@code <img src="cid:内嵌图片文件名" />}
     * @param images
     *            内嵌图片
     * @param attachments
     *            附件
     * @return 如果邮件发送成功，则返回 {@code true}，否则返回 {@code false}
     */
    public boolean send(String to, String cc, String bcc, String subject, String content, File[] images,
            File[] attachments) {
        // 以轮询方式实现负载均衡
        int index = position.updateAndGet(p -> p + 1 < size ? p + 1 : 0);
        // int index = 0;
        log.info("index={}", index);
        Properties properties = propertiesList.get(index);
        log.info("properties={}", properties);
        Session session = sessionList.get(index);
        String personal = properties.getProperty("com.mtdhb.mail.personal");
        String user = properties.getProperty("com.mtdhb.mail.user");
        String password = properties.getProperty("com.mtdhb.mail.password");
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(user, MimeUtility.encodeText(personal)));
            addRecipients(message, Message.RecipientType.TO, to);
            if (cc != null) {
                addRecipients(message, Message.RecipientType.CC, cc);
            }
            if (bcc != null) {
                addRecipients(message, Message.RecipientType.BCC, bcc);
            }
            message.setSubject(subject);
            // 最外层部分
            MimeMultipart wrapPart = new MimeMultipart();
            MimeMultipart htmlWithImageMultipart = new MimeMultipart();
            // 文本部分
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(content, "text/html; charset=UTF-8");
            htmlWithImageMultipart.addBodyPart(htmlPart);
            // 内嵌图片部分
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
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e,
                    "index={}, properties={}, to={}, cc={}, bcc={}, subject={}, content{}, images={}, attachments={}",
                    index, properties, to, cc, bcc, subject, content, images, attachments);
        }
        return false;
    }

    private void addRecipients(MimeMessage message, RecipientType type, String recipients) throws MessagingException {
        String[] addresses = recipients.split(";");
        for (int i = 0; i < addresses.length; i++) {
            message.addRecipients(type, addresses[i]);
        }
    }

    private static class LazyHolder {
        private static final Mail INSTANCE = new Mail();
    }

}