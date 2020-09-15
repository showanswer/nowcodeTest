package com.nowcoder.community.service;

import com.nowcoder.community.entity.User;

public interface UserService {
    //根据id查询
    User selectByid(Integer id);
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
}
