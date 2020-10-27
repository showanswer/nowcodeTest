package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DiscussPostMapper {
    /*分页查询 根据id查询该用户的帖子  offset是每页起始行的行号，limit是每页显示数据的数量
    * 首页不需要加userId  这是为了以后查询方便 我发不过的帖子  */
    List<DiscussPost> selectDiscussPost( int userId,int offset,int limit,int orderMode);

    //@Param()注解用于给参数起别名
    //如果只有一个参数  并且在<if>中使用，则必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);

    //插入帖子
    int insertDiscussPost(DiscussPost discussPost);
    //通过ID查询帖子的详细信息  帖子的回复，点赞，评论数量等等
    DiscussPost selectDiscussPostById(int id);

    //更新评论  通过帖子ID 来进行更新
    int updateCommentCount(int id, int commentCount);
    //更新帖子类型 0-普通; 1-置顶;
    int updateType(int id, int type);
    //更新帖子状态 0-正常; 1-精华; 2-拉黑;
    int updateStatus(int id, int status);

    //更新帖子分数
    int updateScore(int id,double score);

}
