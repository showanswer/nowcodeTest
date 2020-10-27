package com.nowcoder.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * 用来添加缓存  Cookie 中的凭证
 */
public class CookieUtil {

    public static String getValue(HttpServletRequest request, String name) {
        if (request == null || name == null) {
            throw new IllegalArgumentException("参数为空!");
        }
         //循环遍历  cookie缓存 取出值
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        //查询不到返回null
        return null;
    }

}
