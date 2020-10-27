package com.nowcoder.community.entity;

import java.util.HashMap;
import java.util.Map;

public class Event {
    //消息队列的主题  有三种
    private String topic;
    //发送者ID
    private int userId;
    //发送消息的类型  评论 关注 赞
    private int entityType;
    //对那条进行评论、关注、赞 进行事务通知的
    private int entityId;
    //目标对象的那个人的ID
    private int entityUserId;
    //其他信息
    private Map<String, Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

}
