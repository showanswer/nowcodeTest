package com.nowcoder.community.controller;

import com.alibaba.fastjson.JSON;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    // search?keyword=xxx
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model) throws IOException {

        List<Map<String, Object>> discussPostsList =
                elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if(discussPostsList!=null){
            for (int i = 0; i < discussPostsList.size(); i++) {
                Map<String, Object> map = discussPostsList.get(i);
                //把map转换为对象
                DiscussPost post= JSON.parseObject(JSON.toJSONString(map),DiscussPost.class);
                map.put("post",post);
                //帖子作者
                map.put("user",userService.selectById((Integer) map.get("userId")));
                // 查询帖子获赞量
                map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST, (Integer)map.get("id")));
                discussPosts.add(map);
            }

        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("keyword",keyword);
        //分页信息
        page.setLimit(10);
        page.setPath("/search?keyword="+keyword);
        page.setRows(discussPostsList == null ? 0 : elasticsearchService.searchCount(keyword));
        model.addAttribute("page",page);
        return "/site/search";

    }


}
