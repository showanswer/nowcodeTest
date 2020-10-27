package com.nowcoder.community.aspect;

import com.nowcoder.community.controller.advice.ExceptionAdvice;
import org.aopalliance.intercept.Joinpoint;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * AOP记录日志
 */
/*@Component
@Aspect*/
public class ServiceLogAspect {

    private static final Logger loggerr= LoggerFactory.getLogger(ServiceLogAspect.class);

    /*声明在那些地方 执行AOP方法  第一个*：是返回值，可以是任意类型的返回值
    * com.nowcoder.community.service.*： 代表在service包下的所有类中  .*所有的方法中   (..)所有的方法中   */
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut(){

    }

    //前置方法
    @Before("pointcut()")
    public void before(JoinPoint joinPoint){
        //用户 XXX在 什么时间 访问了 com.nowcoder.community.service.XXX()
        //获取request对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //这里有问题：之前设置的是 打印所有的service业务层的方法日志 获取的request对象对象是从控制层得到的 控制层调用service层
        //现在加了kafka 消费者是由生产者 产出数据后自动调用  不经过控制层 所以就无法打印到消费者方法
        if(attributes == null){
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        //获取IP地址
        String ip = request.getRemoteHost();
        //获取当前时间
        String now =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        //获取目标执行的方法或功能名称   访问的是哪个类哪个方法
        /*joinPoint.getSignature().getDeclaringTypeName() 获取的是 类名 */
        /*joinPoint.getSignature().getName() 获取的是类种的方法名*/
       String target= joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName();
       loggerr.info(String.format("用户[%s],在[%s],访问了[%s].",ip,now,target));
    }


}
