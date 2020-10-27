package com.nowcoder.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings(){
        String redisKey="test:count";
        //opsForValue 传递String类型的值
        redisTemplate.opsForValue().set(redisKey,1);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    @Test
    public void testHashes(){
        String redisKey="test:user";
        //opsForHash 传递Hash类型的值
        redisTemplate.opsForHash().put(redisKey,"id",1);
        redisTemplate.opsForHash().put(redisKey,"username","zhangsan");
        //hash redis里是 hget
        System.out.println(redisTemplate.opsForHash().get(redisKey,"id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey,"username"));
    }

    @Test
    public void testLists(){
        String redisKey ="test:ids";
        //opsForList 传递List类型的值   顺序：先进后出
        redisTemplate.opsForList().leftPush(redisKey,101);
        redisTemplate.opsForList().leftPush(redisKey,102);
        redisTemplate.opsForList().leftPush(redisKey,103);
        //大小
        System.out.println(redisTemplate.opsForList().size(redisKey));
        //去第0个索引的值
        System.out.println(redisTemplate.opsForList().index(redisKey,0));
        //查询  范围  这个是查询
        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 2));
        //输出查询的值  顺序：先进后出   即删除
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
    }

    @Test
    public void testSets(){
        String redisKey ="test:teachers";
        //存值
        redisTemplate.opsForSet().add(redisKey,"刘备","关羽","张飞");
        //查询数量
        System.out.println(redisTemplate.opsForSet().size(redisKey));
        //set集合无序   删除也是随机的
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        //查询内容
        System.out.println(redisTemplate.opsForSet().members(redisKey));
    }

    @Test
    public void testSortedSets(){
        String redisKey ="test:student";
        //存值
        redisTemplate.opsForZSet().add(redisKey,"张三",80);
        redisTemplate.opsForZSet().add(redisKey,"李四",90);
        redisTemplate.opsForZSet().add(redisKey,"王五",70);
        redisTemplate.opsForZSet().add(redisKey,"赵六",60);
        //查询数量
        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        //查询 有序set后面的分值
        System.out.println(redisTemplate.opsForZSet().score(redisKey,"张三"));
        //查询排名  按分值排序  默认：从小到大  从0开始
        System.out.println(redisTemplate.opsForZSet().rank(redisKey,"张三"));
        //设置从大到小排序 查询排名  从0开始
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey,"张三"));
        //查询 排名范围内的 值  从小到大  从0开始
        System.out.println(redisTemplate.opsForZSet().range(redisKey,0,1));
        //从大到小  从0开始
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey,0,1));
    }

    @Test
    public void testKeys(){
        //删除
        redisTemplate.delete("test:user");
        System.out.println(redisTemplate.hasKey("test:user"));
        //设置过期时间
        redisTemplate.expire("test:student",10, TimeUnit.SECONDS);
    }
    //多次访问同一个key   绑定key
    @Test
    public void testBoundOperations(){
        String redisKey="test:count";
        //进行绑定
        BoundValueOperations operations= redisTemplate.boundValueOps(redisKey);
        //operations  ==  redisTemplate.opsForValue()
        operations.increment();
        operations.increment();
        operations.increment(3);
        operations.get();
    }
    //redis事务的使用
    @Test
    public void testTransactional(){
      Object object = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String redisKey="test:tx";
                //启动事务
                redisOperations.multi();
                redisOperations.opsForSet().add(redisKey,"zhangsan");
                redisOperations.opsForSet().add(redisKey,"lisi");
                redisOperations.opsForSet().add(redisKey,"wangwu");
                System.out.println(redisOperations.opsForSet().members(redisKey));
                //redisOperations.exec()提交事务
                return redisOperations.exec();
            }
        });
        System.out.println(object);
    }



}
