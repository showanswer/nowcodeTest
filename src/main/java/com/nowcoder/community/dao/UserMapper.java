package com.nowcoder.community.dao;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Repository
public interface UserMapper {
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

}
