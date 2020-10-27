package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.impl.UserServiceImpl;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//注册 登陆功能
@Controller
public class LoginController implements CommunityConstant {

    //打印日志 创建一个日志对象
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private RedisTemplate redisTemplate;


    //页面跳转
    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    //表单提交 页面跳转
    //spring MVC  将参数存放在了model中，再有了model参数的函数中，其余其他参数都会被存放在model中 可随着也买你的跳转 带入其他页面
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model,User user) throws Exception {
        //进行注册，并发送激活邮件
        Map<String, Object> map = userService.register(user);
        //若注册成功
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功,我们已经向您的邮箱发送了一封激活邮件,请尽快激活!");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            //注册失败，返回错误信息
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            model.addAttribute("user", user);
            return "/site/register";
        }
    }
    // http://localhost:8080/community/activation/101/code   邮件  账号激活
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        //进行激活
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功,您的账号已经可以正常使用了!");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作,该账号已经激活过了!");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败,您提供的激活码不正确!");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    //生成验证码
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response/*, HttpSession session*/) {
        // 生成验证码  根据之前的配置随机生成一个字符串
        String text = kaptchaProducer.createText();
        //根据这个字符串 生成以后个验证码图片
        BufferedImage image = kaptchaProducer.createImage(text);

        // 将验证码存入session
        //session.setAttribute("kaptcha", text);
        //验证码的归属
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        // 将验证码存入Redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);

        // 设置返回的内容是一个png的图片
        response.setContentType("image/png");
        try {
            //从response中获取到的输出流 系统回自己关闭
            OutputStream os = response.getOutputStream();
            //输出图片 用os流输出 输出到浏览器上
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("响应验证码失败:" + e.getMessage());
        }
    }


    /*如果参数是一个实体类对象 这种特殊的参数 MVC对把他放在model中，如果是普通参数则从request中获得  */
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password,@CookieValue("kaptchaOwner") String kaptchaOwner,String code, boolean rememberme,
                        Model model,/*HttpSession session,*/HttpServletResponse response){
           //登陆前先判断 是否是第一次登陆 在session中取值 看没有没有验证码
        // String kaptcha = (String) session.getAttribute("kaptcha");
        String kaptcha = null;
        if (StringUtils.isNotBlank(kaptchaOwner)) {
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }

           //如果session中没有验证码 或者传过来的验证码为空 或者两者不相等
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg", "验证码不正确!");
            return "/site/login";
        }
        //检查账号密码
        //判断是否点了记住我,判断cookie失效时间  这是布尔值不用再两边判断 a>b？  不用这么判断了
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;

        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        //判断成功还是失败  ticket是在最后一步插入map的 到哪里就代表成功了
        if (map.containsKey("ticket")){
            //存入cookie中  给客户端发送过去 客户端进行保存
            Cookie cookie=new Cookie("ticket",map.get("ticket").toString());
            //cookie 的有限路径 再进行这个项目访问的时候 才携带cookie
            cookie.setPath(contextPath);
            //cookie的有效时间
            cookie.setMaxAge(expiredSeconds);
            //返回给客户端   下次访问主页就是直接进入这个用户的主页了
            response.addCookie(cookie);
            return "redirect:/index";
        }else {
            //将登陆时出的错 取出再存到里面 返回给前端页面
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }
    //注销登陆  @CookieValue("ticket") String ticket 可以要求spring将值注入
    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }


}
