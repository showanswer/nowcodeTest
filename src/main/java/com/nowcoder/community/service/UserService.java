package com.nowcoder.community.service;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

public interface UserService {
    //根据id查询
    User selectById(Integer id);
    //根据名称查询
    User selectByName(String username);
    // 根据邮件查询
    User selectByEmail(String email);
    //插入数据
    int insertUser(User user);
    //更新角色
    int updateStatus(Integer id,Integer status);
    //更换头像
    int updateHeader(Integer id,String headerUrl);
    //更新密码
    int updatePassword(Integer id,String password);
    //注册
    Map<String,Object> register(User user) throws Exception;
    //登陆
    Map<String, Object> login(String username, String password, int expiredSeconds);
    //退出登录
    void logout(String ticket);
    //找回密码
    LoginTicket findLoginTicket(String ticket);

    //根据用户名 查找用户ID
    User findUserByName(String username);
    //判断用户的权限
    Collection<? extends GrantedAuthority> getAuthorities(int userId);

}
