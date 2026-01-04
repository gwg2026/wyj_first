package nxu.service;

import nxu.entity.Category;

import java.util.List;

/**
 * 分类Service接口
 */
public interface CategoryService {
    /**
     * 查询所有分类
     */
    List<Category> findAll();

    /**
     * 根据ID查询分类
     */
    Category findById(Integer id);
}

