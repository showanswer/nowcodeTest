package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解：使用在方法上  在已经登陆后的controoler方法上使用  代表这个控制方法必须登陆后才能进行访问+
 */
@Target(ElementType.METHOD)   /*该注解用在方法上*/
@Retention(RetentionPolicy.RUNTIME)   /*该注解的运行周期  程序运行时爷有效JVM时也存在*/
public @interface LoginRequired {

}
