package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageMapper {

    // 查询当前用户的会话列表,针对每个会话只返回一条最新的私信.
    List<Message> selectConversations(int userId, int offset, int limit);
    // 查询当前用户的会话数量.
    int selectConversationCount(int userId);

    // 查询某个会话所包含的私信列表.
    List<Message> selectLetters(String conversationId, int offset, int limit);
    // 查询某个会话所包含的私信数量.
    int selectLetterCount(String conversationId);

    // 查询未读私信的数量  这里是动态SQL，可以是总的也可以是个别的
    int selectLetterUnreadCount(int userId, String conversationId);

    //添加私信
    int insertMessage(Message message);
    //改变消息的状态  0：未读  1：已读  2:删除
    int updateStatus(List<Integer> ids,int status);

    //这里的userId 是当前用户的ID  这里功能是 谁登陆了谁显示该功能  当前用户就是接收者
    // 查询某个主题下最新的通知
    Message selectLatestNotice(int userId, String topic);

    // 查询某个主题所包含的通知数量
    int selectNoticeCount(int userId, String topic);

    // 查询未读的通知的数量
    int selectNoticeUnreadCount(int userId, String topic);

    // 查询某个主题所包含的通知列表
    List<Message> selectNotices(int userId, String topic, int offset, int limit);

}
