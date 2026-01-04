package nxu.service.impl;

import nxu.entity.Favorite;
import nxu.service.FavoriteService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 收藏Service实现
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {

    // 模拟数据存储（在实际项目中应该使用数据库）
    private static final List<Favorite> favorites = new ArrayList<>();
    private static int nextId = 1;

    @Override
    public Favorite toggleFavorite(Integer userId, Integer itemId) {
        // 先检查是否已经收藏
        for (Favorite favorite : favorites) {
            if (favorite.getUserId().equals(userId) && favorite.getItemId().equals(itemId)) {
                // 已收藏，删除收藏
                favorites.remove(favorite);
                favorite.setIsFavorited(false);
                return favorite;
            }
        }
        
        // 未收藏，创建收藏
        Favorite favorite = new Favorite();
        favorite.setId(nextId++);
        favorite.setUserId(userId);
        favorite.setItemId(itemId);
        favorite.setCreateTime(new Date());
        favorite.setIsFavorited(true);
        favorites.add(favorite);
        
        return favorite;
    }

    @Override
    public boolean isFavorited(Integer userId, Integer itemId) {
        return favorites.stream()
                .anyMatch(f -> f.getUserId().equals(userId) && f.getItemId().equals(itemId));
    }

    @Override
    public List<Favorite> findByUserId(Integer userId) {
        List<Favorite> userFavorites = new ArrayList<>();
        for (Favorite favorite : favorites) {
            if (favorite.getUserId().equals(userId)) {
                userFavorites.add(favorite);
            }
        }
        return userFavorites;
    }

    @Override
    public int deleteByUserIdAndItemId(Integer userId, Integer itemId) {
        return favorites.removeIf(favorite -> 
            favorite.getUserId().equals(userId) && favorite.getItemId().equals(itemId)) ? 1 : 0;
    }
}