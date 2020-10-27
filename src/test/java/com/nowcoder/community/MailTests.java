package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {
    @Autowired
    private MailClient mailClient;
    /*注入 thymeleaf 的模板引擎   使用模板引擎的主要目的：生成动态网页*/
    @Autowired
    private TemplateEngine templateEngine;


    @Test
    public void testTextMail(){
        mailClient.sendMail("2060465803@qq.com","Test","Welcome");
    }
    @Test
    public void testHtmlMail(){
        //这个作用是，是向Thymeleaf中  动态输入值
        Context context=new Context();
        context.setVariable("username","sandy");
        //调用模板引擎  生成动态网页
        String content=templateEngine.process("/mail/demo",context);
        System.out.println(content);
        mailClient.sendMail("2060465803@qq.com","Html",content);

    }


}
