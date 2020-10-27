package com.nowcoder.community;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoggerTest {
   //将这个类 设置实例化
    private static final Logger logger= LoggerFactory.getLogger(LoggerTest.class);

    @Autowired
    private UserServiceImpl userService;

    @Test
    public void testLogger(){
        System.out.println(logger.getName());
        /*设置日志级别*/
        logger.debug("debug log");
        logger.info("info log");
        logger.warn("warn log");
        logger.error("error log");
    }

    @Test
    public void testUser(){
        System.out.println(userService.findUserByName("root"));
    }


}
