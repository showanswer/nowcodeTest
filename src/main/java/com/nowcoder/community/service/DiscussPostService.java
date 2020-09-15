package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


public interface DiscussPostService {
    /*分页查询 根据id查询该用户的帖子  offset是每页起始行的行号，limit是每页显示数据的数量*/
    List<DiscussPost> selectDiscussPost(int userId, int offset, int limit);

    //@Param()注解用于给参数起别名
    //如果只有一个参数  并且在<if>中使用，则必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);


}
