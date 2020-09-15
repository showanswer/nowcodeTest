package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.impl.DiscussPostServiceImpl;
import com.nowcoder.community.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private DiscussPostServiceImpl discussPostService;
    @Autowired
    private UserServiceImpl userService;

    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        /**
         * 方法调用钱,SpringMVC会自动实例化Model和Page,并将Page注入Model.
         *  所以,在thymeleaf中可以直接访问Page对象中的数据.
         */
        //首页时游客登陆
        page.setRows(discussPostService.selectDiscussPostRows(0));
        page.setPath("/index");
        //分页查询
        List<DiscussPost> list = discussPostService.selectDiscussPost(0, page.getOffset(), page.getLimit());
        //将查询到的结果进行封装 创建一个list数组，元素对象都是map集合
        List<Map<String,Object>> discussPost=new ArrayList<>();
        if(list != null){
            //若查询到了数据
            for (DiscussPost post : list) {
                Map<String,Object> map=new HashMap<>();
                //将该用户的评论数据添加到map集合中 将DiscussPost全部变为字符串类型
                map.put("post",post);
                User user = userService.selectByid(post.getUserId());
                //将该用户的个人信息存储到Map集合中
                map.put("user",user);
                discussPost.add(map);
            }
        }
        model.addAttribute("discussPost",discussPost);
       return "index";

    }


}
