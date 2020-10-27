package com.nowcoder.community;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.elasticsearch.index.mapper.SourceToParse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.context.event.annotation.BeforeTestClass;

//单元测试
@SpringBootTest
public class SpringbootTest {

    //再类初始化之前执行
    @BeforeTestClass
    public static void beforeClass(){
        System.out.println("beforeTestClass");
    }

    @AfterTestClass
    public static void afterClass(){
        System.out.println("afterTestClass");
    }

    @Before("test1")
    public void before(){
        System.out.println("before");
    }

    @After("test1")
    public void after(){
        System.out.println("after");
    }

    @Test
    public void test1(){
        System.out.println("test1...");
    }
    @Test
    public void test2(){
        System.out.println("test2...");
    }

}
