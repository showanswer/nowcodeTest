package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DataService {

    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    //将指定的IP计入UV  单日UV
    public void recordUV(String ip){
        String redisKey = RedisKeyUtil.getUVKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey, ip);
    }
    // 统计指定日期范围内的UV
    public long calculateUV(Date start, Date end){
        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        // 整理该日期范围内的key
        List<String> keyList = new ArrayList<>();
        // YEAR、MONTH、DAY_OF_MONTH、HOUR 等日历字段之间的转换提供了一些方法，并为操作日历字段（例如获得下星期的日期）提供了一些方法
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        //当时间<=end 时 循环
        while (!calendar.getTime().after(end)){
            //获取那一天的key
            String key = RedisKeyUtil.getUVKey(df.format(calendar.getTime()));
            //将起始那一天的key存到list中
            keyList.add(key);
            //天数时间+1天
           calendar.add(Calendar.DATE,1);
        }
        //   指定日期范围内的UV的key
        String redisKey = RedisKeyUtil.getUVKey(df.format(start), df.format(end));
        //  合并这些数据 将查询到的数据 存到redisKey 中   从这些key集合 keyList.toArray() 中
        redisTemplate.opsForHyperLogLog().union(redisKey, keyList.toArray());
        // 返回统计的结果
        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    // 将指定用户计入DAU
    public void recordDAU(int userId) {
        String redisKey = RedisKeyUtil.getDAUKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey, userId, true);
    }
    // 统计指定日期范围内的DAU
    public long calculateDAU(Date start, Date end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }

        // 整理该日期范围内的key
        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)) {
            String key = RedisKeyUtil.getDAUKey(df.format(calendar.getTime()));
            keyList.add(key.getBytes());
            calendar.add(Calendar.DATE, 1);
        }

        // 进行OR运算
        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                //key
                String redisKey = RedisKeyUtil.getDAUKey(df.format(start), df.format(end));
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(), keyList.toArray(new byte[0][0]));
                return connection.bitCount(redisKey.getBytes());
            }
        });
    }

}
