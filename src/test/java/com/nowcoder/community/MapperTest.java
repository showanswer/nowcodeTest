package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.impl.DiscussPostServiceImpl;
import com.nowcoder.community.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.annotation.Validated;

import java.util.Date;
import java.util.List;
import java.util.Random;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private DiscussPostServiceImpl discussPostService;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelect(){
        User user = userService.selectById(101);
        System.out.println(user);
        System.out.println(userService.selectByName("liubei"));
        System.out.println(userService.selectByEmail("nowcoder101@sina.com"));
    }
    @Test
    public void testInsert(){
        User user=new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }
    @Test
    public void updateUser(){
        int status = userMapper.updateStatus(150, 1);
        System.out.println(status);

        status=userMapper.updateHeader(150,"http://www.nowcoder.com/102.png");
        System.out.println(status);

        status=userMapper.updatePassword(150,"hello");
        System.out.println(status);
    }

    @Test
    public void testSelectPostS(){
        List<DiscussPost> list = discussPostService.selectDiscussPost(149, 0, 10,0);
        for (DiscussPost discussPost : list) {
            System.out.println(discussPost);
        }

        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    @Test
    public void test(){
        int rows = discussPostService.selectDiscussPostRows(0);
        System.out.println(rows);
    }

    @Test
    public void test1(){
        System.out.println(new Random().nextInt(10));
    }
    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket() {
        //根据用户的凭证来查询数据库
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("abc", 1);
        loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }

    @Test
    public void testSelectLetters(){
        List<Message> list = messageMapper.selectConversations(111, 0, 10);
        for (Message message : list) {
            System.out.println(message);
        }
        System.out.println(messageMapper.selectConversationCount(111));
        List<Message> letters = messageMapper.selectLetters("111_112", 0, 10);
        for (Message letter : letters) {
            System.out.println(letter);
        }
        System.out.println(messageMapper.selectLetterCount("111_112"));
        System.out.println(messageMapper.selectLetterUnreadCount(131, "111_131"));
    }


}
