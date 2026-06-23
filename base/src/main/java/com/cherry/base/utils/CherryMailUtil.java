package com.cherry.base.utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.List;
import java.util.Properties;

/**
 * JavaMail工具类，用于在Java中发送邮件
 */
public class CherryMailUtil {

    /**
     * 邮箱发送账号，用于登录 SMTP 服务器的邮箱地址。
     */
    public final static String USERNAME = "cherrycoast@163.com";

    /**
     * 邮箱平台的授权码，用于身份验证。
     * 注意：授权码不同于邮箱密码，需在邮箱设置中获取。
     */
    public final static String PASSWORD = "TCjN8329GZp24WTJ";

    /**
     * SMTP 服务器地址，用于发送邮件。
     * 这里使用的是 Gmail 的 SMTP 服务器。
     */
    public final static String HOST = "smtp.163.com";

    public static Session session = null;


    /**
     * 创建并初始化邮件会话（Session）。
     * <p>
     * 如果 Session 已经存在，则直接返回，否则使用提供的 SMTP 配置初始化一个新的 Session。
     * 配置包括 SMTP 服务器地址、端口、是否需要认证，以及是否启用 STARTTLS。
     * 该方法确保发送邮件时总是有一个有效的 Session 实例。
     */
    public static void createSession() {
        if (session != null) return;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", "465"); // ✅ 使用SSL端口
        props.put("mail.smtp.ssl.enable", "true"); // ✅ 启用SSL
        props.put("mail.smtp.ssl.trust", HOST); // ✅ 信任SMTP服务器

        session = Session.getInstance(
                props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USERNAME, PASSWORD);
                    }
                });
    }

    /**
     * 发送电子邮件给多个收件人。
     *
     * @param title      邮件标题
     * @param recipients 收件人邮箱地址
     * @throws RuntimeException 当邮件发送失败时抛出此异常
     */
    public static void postMessage(String title, String content, List<String> recipients) throws MessagingException {
        createSession();

        InternetAddress[] internetAddresses = new InternetAddress[recipients.size()];
        for (int i = 0; i < recipients.size(); i++) {
            internetAddresses[i] = new InternetAddress(recipients.get(i));
        }

        MimeMessage message = new MimeMessage(session);
        message.setSubject(title, "UTF-8");
        message.setText(content, "UTF-8", "html");
        message.setFrom(new InternetAddress(USERNAME));
        message.setRecipients(MimeMessage.RecipientType.TO, internetAddresses);
        Transport.send(message);
    }

}

