package com.nowcoder.community.service.impl;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService ,CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    //这楼里本地地址代替域名
    @Value("${community.path.domain}")
    private String domain;
    //项目名
    @Value("${server.servlet.context-path}")
    private String contextPath;

    //@Autowired
   // private LoginTicketMapper loginTicketMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    @Override
    public User selectById(Integer id) {
        //return userMapper.selectById(id);
        User user=getCache(id);
        if(user==null){
            initCache(id);
        }
        return user;
    }

    @Override
    public User selectByName(String username) {
        return userMapper.selectByName(username);
    }

    @Override
    public User selectByEmail(String email) {
        return userMapper.selectByEmail(email);
    }

    @Override
    public int insertUser(User user) {
        return userMapper.insertUser(user);
    }

    @Override
    public int updateStatus(Integer id, Integer status) {
        int rows= userMapper.updateStatus(id,status);
        clearCache(id);
        return rows;
    }

    @Override
    public int updateHeader(Integer id, String headerUrl) {
      int rows=  userMapper.updateHeader(id,headerUrl);
        clearCache(id);
        return rows;
    }

    @Override
    public int updatePassword(Integer id, String password) {
       int rows= userMapper.updatePassword(id,password);
        clearCache(id);
        return rows;
    }

    //注册功能
    @Override
    public Map<String,Object> register(User user) throws Exception {
        Map<String,Object> map=new HashMap<>();
        //空值判断处理
        if(user == null){
            throw new IllegalAccessException("参数不能为空!");
        }
        //获取用户名，密码，邮箱 判断输入的时候是否为空
        if(StringUtils.isBlank(user.getUsername())){
             map.put("usernameMsg","账号不能为空");
             return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }

        //验证账号是否存在
        User u= userMapper.selectByName(user.getUsername());
        if(u!=null){
            map.put("usernameMsg","该账号已存在");
            return map;
        }
        //验证邮箱是否存在
        u=userMapper.selectByEmail(user.getEmail());
        if(u!=null){
            map.put("emailMsg","该邮箱已存在");
            return map;
        }

        //注册用户  添加用户盐值
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        //所以输入数据库的密码都需要进行盐值加密
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        //设置新用户的默认
        user.setType(0);    //新注册 都为普通用户 0
        user.setStatus(0);   //新注册都是未激活状态0
        user.setActivationCode(CommunityUtil.generateUUID());   //添加一个激活码
        //为新用户设置随机头像  String.format(a，b)  将a参数里的/%替换为b参数的值
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        //设置用户创建时间
        user.setCreateTime(new Date());
        //将注册用户的信息 插入到数据库中
        userMapper.insertUser(user);

        //已经完成了注册功能  插入了数据 只是还没有激活   发送激活文件
        Context context=new Context();
        context.setVariable("email",user.getEmail());
        //设置激活url路径    http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        //生成动态邮件页面 并将context设置的参数传进去
        String content = templateEngine.process("/mail/activation", context);
        //发送邮件
        mailClient.sendMail(user.getEmail(), "激活账号", content);
        return map;
    }

    //激活功能
    public int activation(int userId, String code) {

        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    //登陆功能   有多种可能性会导致失败用Map
    public Map<String, Object> login(String username, String password, int expiredSeconds){
        Map<String, Object> map = new HashMap<>();
        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        // 验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在!");
            return map;
        }
        //到这里说明 账号正确没重复  验证状态
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活!");
            return map;
        }
        // 验证密码  将你输入的密码进行盐值加密  然后与数据库中存储的盐值密码进行比较
       password=CommunityUtil.md5(password+user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg", "密码不正确!");
            return map;
        }

        //到这里说明登陆成功了 需要发放一个登陆凭证  证明你是登陆的
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        //随机生成一个登陆凭证
        loginTicket.setTicket(CommunityUtil.generateUUID());
        //状态为登陆状态
        loginTicket.setStatus(0);
        //创建一个时间 为当前时间并向后推10分钟的时间 为到期时间
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds * 1000));
        //loginTicketMapper.insertLoginTicket(loginTicket);
        //生成凭证验证码
        String redisKey= RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        //他会把对象序列化为json字符串
        redisTemplate.opsForValue().set(redisKey,loginTicket);

        //将这个用户的凭证放入数据库
        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    //退出登录
    public void logout(String ticket) {
        //设置状态为1  未登录状态
        //loginTicketMapper.updateStatus(ticket, 1);
        String redisKey= RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket= (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey,loginTicket);
    }
    //查询凭证
    @Override
    public LoginTicket findLoginTicket(String ticket) {
       // return loginTicketMapper.selectByTicket(ticket);
        String redisKey= RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    //1.优先从缓存中取值
    private User getCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }
    //2.取不到就初始化缓存数据 从数据库中取值存入redis中
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }
    //3.数据更新时，清除缓存数据
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

    //判断  用户的类型
    public Collection<? extends GrantedAuthority> getAuthorities(int userId){
        User user = this.selectById(userId);
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                switch (user.getType()) {
                    case 1:
                        return AUTHORITY_ADMIN;
                    case 2:
                        return AUTHORITY_MODERATOR;
                    default:
                        return AUTHORITY_USER;
                }
            }
        });
        return list;
    }



}
