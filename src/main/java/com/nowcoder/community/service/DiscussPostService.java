package com.nowcoder.community.service;


import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Param;



import java.util.List;


public interface DiscussPostService {
    /*分页查询 根据id查询该用户的帖子  offset是每页起始行的行号，limit是每页显示数据的数量*/
    List<DiscussPost> selectDiscussPost(int userId, int offset, int limit,int orderMode);

    //@Param()注解用于给参数起别名
    //如果只有一个参数  并且在<if>中使用，则必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);

    //添加帖子数据
    int addDiscussPost(DiscussPost post);

    //查询帖子Id查询帖子
    DiscussPost findDiscussPostById(int id);

    //更新帖子的数量
    int updateCommentCount(int id, int commentCount);

    //更新帖子类型 0-普通; 1-置顶;
    int updateType(int id, int type);
    //更新帖子状态 0-正常; 1-精华; 2-拉黑;
    int updateStatus(int id, int status);

    //更新帖子分数
    int updateScore(int id,double score);
}
