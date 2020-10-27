package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 自定义注解的使用 ： LoginRequired自定义好后 运行的环境和作用
 * 进行了验证  无法从地址栏输入地址进入个人后台 必须进行登陆
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;


    /*应该在调用controller之前 访问之处及进行拦截*/
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /*判断 这个拦截的目标 handler 是否是方法 */
        if(handler instanceof HandlerMethod){
            //将拦截目标进行转型  HandlerMethod:获取由方法和bean组成的处理程序方法的信息 。提供对方法参数，方法返回值，方法注释等的便捷访问
            HandlerMethod handlerMethod= (HandlerMethod) handler;
            //获取其中的方法对象
            Method method = handlerMethod.getMethod();
            //查看方法上是否使用了LoginRequired这个注解  检查注解
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            //如果这个方法上面加了 即loginRequired不为空 但 不能够自从这个请求的线程中获取到 user信息 就无法进行访问某些地址
            if(loginRequired != null && hostHolder.getUser()==null){
                //强制重定向到登陆界面   返回跳转到登陆界面
                response.sendRedirect(request.getContextPath()+ "/login");
                return false;
            }
        }
        return true;
    }
}
