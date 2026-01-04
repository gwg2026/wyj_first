package nxu.service;

import nxu.entity.LostItem;

import java.util.List;

/**
 * 失物信息Service接口
 */
public interface LostItemService {
    /**
     * 发布失物信息
     */
    int publish(LostItem lostItem);

    /**
     * 根据ID查询失物信息
     */
    LostItem findById(Integer id);

    /**
     * 查询所有失物信息
     */
    List<LostItem> findAll();

    /**
     * 根据类型查询
     */
    List<LostItem> findByType(Integer type);

    /**
     * 查询最新的失物信息
     */
    List<LostItem> findLatest(Integer limit);

    /**
     * 查询最热的失物信息
     */
    List<LostItem> findHot(Integer limit);

    /**
     * 查询相关推荐物品
     */
    List<LostItem> findRelated(Integer excludeId, Integer categoryId, Integer type);


    List<LostItem> findByCategory(Integer categoryId);

    /**
     * 搜索失物信息
     */
    List<LostItem> search(String keyword, Integer categoryId);

    /**
     * 更新失物信息
     */
    int update(LostItem lostItem);

    /**
     * 更新状态
     */
    int updateStatus(Integer id, Integer status);

    /**
     * 查看详情（增加浏览次数）
     */
    LostItem viewDetail(Integer id);

    /**
     * 根据用户ID查询
     */
    List<LostItem> findByUserId(Integer userId);

    /**
     * 根据状态查询
     */
    List<LostItem> findByStatus(Integer status);

    /**
     * 删除失物信息
     */
    int deleteById(Integer id);
}

