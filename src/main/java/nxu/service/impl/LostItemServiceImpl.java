package nxu.service.impl;

import nxu.dao.LostItemMapper;
import nxu.entity.LostItem;
import nxu.service.LostItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 失物信息Service实现类
 */
@Service
public class LostItemServiceImpl implements LostItemService {

    @Autowired
    private LostItemMapper lostItemMapper;

    @Override
    public int publish(LostItem lostItem) {
        if (lostItem.getStatus() == null) {
            lostItem.setStatus(0);  // 默认未找到
        }
        if (lostItem.getViewCount() == null) {
            lostItem.setViewCount(0);  // 默认浏览次数为0
        }
        return lostItemMapper.insert(lostItem);
    }

    @Override
    public LostItem findById(Integer id) {
        return lostItemMapper.findById(id);
    }

    @Override
    public List<LostItem> findAll() {
        return lostItemMapper.findAll();
    }

    @Override
    public List<LostItem> findByType(Integer type) {
        return lostItemMapper.findByType(type);
    }

    @Override
    public List<LostItem> findLatest(Integer limit) {
        return lostItemMapper.findLatest(limit);
    }

    @Override
    public List<LostItem> findHot(Integer limit) {
        return lostItemMapper.findHot(limit);
    }

                            @Override
    public List<LostItem> findByCategory(Integer categoryId) {
        return lostItemMapper.findByCategory(categoryId);
    }

    @Override
    public List<LostItem> search(String keyword, Integer categoryId) {
        if (keyword == null || keyword.trim().isEmpty()) {
            if (categoryId != null) {
                return findByCategory(categoryId);
            } else {
                return findAll();
            }
        }
        if (categoryId != null) {
            return lostItemMapper.searchByKeywordAndCategory(keyword.trim(), categoryId);
        } else {
            return lostItemMapper.search(keyword.trim());
        }
    }

    @Override
    public int update(LostItem lostItem) {
        return lostItemMapper.update(lostItem);
    }

    @Override
    public int updateStatus(Integer id, Integer status) {
        return lostItemMapper.updateStatus(id, status);
    }

    @Override
    public LostItem viewDetail(Integer id) {
        // 增加浏览次数
        lostItemMapper.increaseViewCount(id);
        // 查询详情
        return lostItemMapper.findById(id);
    }

    @Override
    public List<LostItem> findByUserId(Integer userId) {
        return lostItemMapper.findByUserId(userId);
    }

    @Override
    public List<LostItem> findByStatus(Integer status) {
        return lostItemMapper.findByStatus(status);
    }

    @Override
    public int deleteById(Integer id) {
        return lostItemMapper.deleteById(id);
    }

    @Override
    public List<LostItem> findRelated(Integer excludeId, Integer categoryId, Integer type) {
        return lostItemMapper.findRelated(excludeId, categoryId, type);
    }
}

