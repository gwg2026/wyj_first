package nxu.entity;

import java.util.Date;

/**
 * 收藏实体类
 */
public class Favorite {
    private Integer id;
    private Integer userId;
    private Integer itemId;
    private Date createTime;

    // 临时字段，用于返回收藏状态（不存储在数据库）
    private boolean isFavorited;

    public Favorite() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public boolean isIsFavorited() {
        return isFavorited;
    }

    public void setIsFavorited(boolean isFavorited) {
        this.isFavorited = isFavorited;
    }
}