package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 用来生成随机字符串和 进行盐值加密  json数据处理
 */
public class CommunityUtil {

    //生成随机字符串
    public static  String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //MD5 盐值加密  这里的key是 密码 不是id号
    public static String md5(String key){
        //判断值是否为空
        if(StringUtils.isBlank(key)){
              return null;
        }
        //md5 加密的方法
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    //处理json字符串 将字符转化尾json格式
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "zhangsan");
        map.put("age", 25);
        System.out.println(getJSONString(0, "ok", map));
    }

}
