package com.nowcoder.community.util;

/**
 * 用来生成redis里的key
 */
public class RedisKeyUtil {

    private static final String SPLIT=":";
    //帖子 回复 都可以进行点赞都是实体 这个实体的前缀
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    //目标   A关注B   B就是A关注的目标
    private static final String PREFIX_FOLLOWEE = "followee";
    //粉丝   A关注B   A就是B的粉丝
    private static final String PREFIX_FOLLOWER = "follower";
    //验证码存放在redis中
    private static final String PREFIX_KAPTCHA = "kaptcha";
    //登陆凭证 也存在redis里
    private static final String PREFIX_TICKET = "ticket";
    //用户信息缓存放在redis中
    private static final String PREFIX_USER = "user";
    //独立用户 根据IP地址来统计
    private static final String PREFIX_UV = "uv";
    //日活跃量 一天内IP的访问量
    private static final String PREFIX_DAU = "dau";
    //存帖子的统计分数
    private static final String PREFIX_POST = "post";

    // 某个实体的赞
    // like:entity:entityType:entityId -> set(userId)  用集合可以看到谁点的赞
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }
    //某个用户的赞
    //like:user:userId -> int
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE+SPLIT+userId;
    }

    // 某个用户关注的实体
    // followee:userId:entityType -> zset(entityId,now)
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    // 某个实体拥有的粉丝
    // follower:entityType:entityId -> zset(userId,now)
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    //登陆验证码
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }
    // 登录的凭证
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    // 用户
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }

    // 单日UV
    public static String getUVKey(String date) {
        return PREFIX_UV + SPLIT + date;
    }

    // 区间UV
    public static String getUVKey(String startDate, String endDate) {
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }

    // 单日活跃用户
    public static String getDAUKey(String date) {
        return PREFIX_DAU + SPLIT + date;
    }

    // 区间活跃用户
    public static String getDAUKey(String startDate, String endDate) {
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }

    // 帖子分数
    public static String getPostScoreKey() {
        return PREFIX_POST + SPLIT + "score";
    }

}
