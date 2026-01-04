package nxu.service;

import nxu.entity.Comment;
import nxu.entity.CommentReply;

import java.util.List;

/**
 * 评论Service接口
 */
public interface CommentService {
    /**
     * 添加评论
     */
    int addComment(Comment comment);

    /**
     * 根据失物ID查询评论列表
     */
    List<Comment> findByItemId(Integer itemId);

    /**
     * 删除评论
     */
    int deleteById(Integer id);

    /**
     * 根据用户ID查询评论数量
     */
    int countByUserId(Integer userId);

    /**
     * 切换评论点赞状态
     */
    boolean toggleLike(Integer userId, Integer commentId);
    
    /**
     * 更新评论点赞数
     */
    int updateLikes(Integer commentId, Integer delta);
    
    /**
     * 获取评论点赞数
     */
    int getLikes(Integer commentId);
    
    /**
     * 添加回复
     */
    int addReply(CommentReply commentReply);
    
    /**
     * 根据评论ID查询回复列表
     */
    List<CommentReply> findRepliesByCommentId(Integer commentId);
}