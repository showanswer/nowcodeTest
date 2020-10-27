package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/*@Deprecated  不推荐使用废弃掉了*/
@Repository
@Deprecated
public interface LoginTicketMapper {
    //方式二：注解的方式与数据库交互

    //登陆成功后要插入一个凭证
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    /*option注解 设置主键为自动生成 因为时注解所以之前的配置文件里的主键自增不生效 需要在设置*/

    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    //根据凭证查找数据 返回一条数据
    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    //删除数据 就是修改用户状态 保留数据为了以后使用
    @Update({
            "<script>",
            "update login_ticket set status=#{status} where ticket=#{ticket} ",
            "<if test=\"ticket!=null\"> ",
            "and 1=1 ",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket, int status);
}
