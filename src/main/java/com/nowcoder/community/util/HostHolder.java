package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息,用于代替session对象.   用来获取当前用户的信息  该信息在登陆的时候舅呗存储到线程之中了
 */
@Component
public class HostHolder {
    //创建私有线程的局部变量    ThreadLocal是每一个线程所单独持有的
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }

}
