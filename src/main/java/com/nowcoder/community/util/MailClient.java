package com.nowcoder.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 *  用来 发送邮件，在邮件中添加HTML页面 添加信息
 */
@Component
public class MailClient {

    private static final Logger logger= LoggerFactory.getLogger(MailClient.class);

    @Autowired
    public JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to,String subject,String content ){

        try {
            //创建邮箱模板
            MimeMessage mimeMessage=mailSender.createMimeMessage();
            //根据模板 创建邮箱
            MimeMessageHelper helper=new MimeMessageHelper(mimeMessage);
            //设置发件人，收件人，邮件主题，邮件内容
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content,true);
            //发送邮箱
            mailSender.send(helper.getMimeMessage());

        } catch (MessagingException e) {
           logger.error("发送邮件失败："+e.getMessage());
        }


    }


}
