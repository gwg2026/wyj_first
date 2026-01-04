package nxu.service;

import nxu.entity.Favorite;

/**
 * 收藏Service接口
 */
public interface FavoriteService {
    /**
     * 切换收藏状态
     */
    Favorite toggleFavorite(Integer userId, Integer itemId);

    /**
     * 检查是否已收藏
     */
    boolean isFavorited(Integer userId, Integer itemId);

    /**
     * 根据用户ID查询收藏列表
     */
    java.util.List<Favorite> findByUserId(Integer userId);

    /**
     * 删除收藏
     */
    int deleteByUserIdAndItemId(Integer userId, Integer itemId);
}