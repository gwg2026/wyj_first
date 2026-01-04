package nxu.entity;

import java.util.Date;
import java.util.List;

/**
 * 评论实体类
 */
public class Comment {
    private Integer id;
    private Integer itemId;
    private Integer userId;
    private String content;
    private Integer likes;
    private Date createTime;
    private Date updateTime;

    // 关联对象（用于查询）
    private User user;
    
    // 关联回复（用于显示）
    private List<CommentReply> replies;

    public Comment() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<CommentReply> getReplies() {
        return replies;
    }

    public void setReplies(List<CommentReply> replies) {
        this.replies = replies;
    }
}