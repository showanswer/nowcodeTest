package com.nowcoder.community.entity;

import java.util.Date;

/**
 * 所有的评论  回复
 */
public class Comment {
    //评论ID
    private int id;
    //评论用户ID
    private int userId;
    //对什么做出评论  1：帖子  2：评论 3：用户  4：题目  5：课程  这里只用1，2
    private int entityType;
    //你对那个帖子做出评论  帖子的ID
    private int entityId;
    //目标ID  也可能是评论下面对别人评论  指向用户ID
    private int targetId;
    //内容
    private String content;
    //状态 0：激活可用  1：禁用
    private int status;
    //评论创建时间
    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", userId=" + userId +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", targetId=" + targetId +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }
}
