package nxu.dao;

import nxu.entity.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类Mapper接口
 */
public interface CategoryMapper {
    /**
     * 查询所有分类
     */
    List<Category> findAll();

    /**
     * 根据ID查询分类
     */
    Category findById(@Param("id") Integer id);
}

