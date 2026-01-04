package nxu.dao;

import nxu.entity.LostItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 失物信息Mapper接口
 */
public interface LostItemMapper {
    /**
     * 插入失物信息
     */
    int insert(LostItem lostItem);

    /**
     * 根据ID查询失物信息（包含用户和分类信息）
     */
    LostItem findById(@Param("id") Integer id);

    /**
     * 查询所有失物信息（包含用户和分类信息）
     */
    List<LostItem> findAll();

    /**
     * 根据类型查询（1-失物，2-拾物）
     */
    List<LostItem> findByType(@Param("type") Integer type);

    /**
     * 查询最新的失物信息（按创建时间倒序）
     */
    List<LostItem> findLatest(@Param("limit") Integer limit);

    /**
     * 查询最热的失物信息（按浏览次数倒序）
     */
    List<LostItem> findHot(@Param("limit") Integer limit);

    /**
     * 根据分类查询
     */
    List<LostItem> findByCategory(@Param("categoryId") Integer categoryId);

    /**
     * 搜索失物信息（按关键字）
     */
    List<LostItem> search(@Param("keyword") String keyword);

    /**
     * 根据关键字和分类搜索
     */
    List<LostItem> searchByKeywordAndCategory(@Param("keyword") String keyword, @Param("categoryId") Integer categoryId);

    /**
     * 更新失物信息
     */
    int update(LostItem lostItem);

    /**
     * 更新状态
     */
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    /**
     * 增加浏览次数
     */
    int increaseViewCount(@Param("id") Integer id);

    /**
     * 根据用户ID查询
     */
    List<LostItem> findByUserId(@Param("userId") Integer userId);

    /**
     * 根据状态查询（0-未找到，1-已找到）
     */
    List<LostItem> findByStatus(@Param("status") Integer status);

    /**
     * 删除失物信息
     */
    int deleteById(@Param("id") Integer id);

    /**
     * 查询相关推荐物品（排除指定ID，优先同分类，其次同类型）
     */
    List<LostItem> findRelated(@Param("excludeId") Integer excludeId, 
                              @Param("categoryId") Integer categoryId, 
                              @Param("type") Integer type);
}

