package com.nowcoder.community.entity;

import java.util.Date;

/**
 * @author coolsen
 * @version 1.0.0
 * @ClassName Comment.java
 * @Description 评论实体类
 * @createTime 5/7/2020 4:27 PM
 */
public class Comment {
    private int id;
    private int userId;//评论的用户id
    private int entityType;//1表示对帖子的评论，2表示对帖子评论的评论
    private int entityId;//entityType为1，表示对帖子的评论，则entityId表示对应discuss_post的表格的id；entityType为2，该评论是针对哪一条评论的评论(entityId具体在comment表格里查找id)
    private int targetId;//0表示没有具体回复谁，有具体值表示回复的用户的userid
    private String content;
    private int status;
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
