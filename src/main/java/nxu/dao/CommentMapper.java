package nxu.dao;

import nxu.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论Mapper接口
 */
@Mapper
public interface CommentMapper {
    /**
     * 插入评论
     */
    int insert(Comment comment);

    /**
     * 根据ID查询评论
     */
    Comment findById(@Param("id") Integer id);

    /**
     * 根据失物ID查询评论列表（包含用户信息）
     */
    List<Comment> findByItemId(@Param("itemId") Integer itemId);

    /**
     * 根据用户ID查询评论列表
     */
    List<Comment> findByUserId(@Param("userId") Integer userId);

    /**
     * 删除评论
     */
    int deleteById(@Param("id") Integer id);

    /**
     * 根据失物ID删除评论
     */
    int deleteByItemId(@Param("itemId") Integer itemId);

    /**
     * 根据用户ID删除评论
     */
    int deleteByUserId(@Param("userId") Integer userId);

    /**
     * 统计评论总数
     */
    int count();

    /**
     * 根据失物ID统计评论数量
     */
    int countByItemId(@Param("itemId") Integer itemId);

    /**
     * 根据用户ID统计评论数量
     */
    int countByUserId(@Param("userId") Integer userId);

    /**
     * 获取评论的点赞数
     */
    int getLikes(@Param("commentId") Integer commentId);

    /**
     * 更新评论的点赞数
     */
    int updateLikes(@Param("commentId") Integer commentId, @Param("delta") Integer delta);
}