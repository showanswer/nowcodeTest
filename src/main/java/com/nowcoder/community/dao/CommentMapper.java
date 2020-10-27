package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentMapper {
    //分页查询   评论下面 还有很多的评论  需要分页显示
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    //查询数据的品论数
    int selectCountByEntity(int entityType, int entityId);

    //插入评论
    int insertComment(Comment comment);

    // 通过评论ID 查找评论信息
    Comment selectCommentById(int id);
}
