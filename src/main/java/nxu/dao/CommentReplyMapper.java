package nxu.dao;

import nxu.entity.CommentReply;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 评论回复Mapper接口
 */
@Mapper
public interface CommentReplyMapper {
    
    /**
     * 插入评论回复
     */
    int insert(CommentReply commentReply);
    
    /**
     * 根据评论ID查询回复列表
     */
    List<CommentReply> findByCommentId(Integer commentId);
    
    /**
     * 删除回复
     */
    int deleteById(Integer id);
    
    /**
     * 根据评论ID删除回复
     */
    int deleteByCommentId(Integer commentId);
    
    /**
     * 统计回复总数
     */
    int count();
    
    /**
     * 根据评论ID统计回复数量
     */
    int countByCommentId(Integer commentId);
}