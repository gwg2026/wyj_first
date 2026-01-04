package nxu.service.impl;

import nxu.dao.CommentMapper;
import nxu.dao.CommentReplyMapper;
import nxu.entity.Comment;
import nxu.entity.CommentReply;
import nxu.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评论Service实现
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private CommentReplyMapper commentReplyMapper;

    @Override
    public int addComment(Comment comment) {
        // 设置创建时间为当前时间
        comment.setCreateTime(new java.util.Date());
        return commentMapper.insert(comment);
    }

    @Override
    public List<Comment> findByItemId(Integer itemId) {
        List<Comment> comments = commentMapper.findByItemId(itemId);
        
        // 为每个评论加载回复数据
        for (Comment comment : comments) {
            List<CommentReply> replies = commentReplyMapper.findByCommentId(comment.getId());
            comment.setReplies(replies);
        }
        
        return comments;
    }

    @Override
    public int deleteById(Integer id) {
        return commentMapper.deleteById(id);
    }

    @Override
    public int countByUserId(Integer userId) {
        return commentMapper.countByUserId(userId);
    }

    @Override
    public boolean toggleLike(Integer userId, Integer commentId) {
        // 实际项目中应该创建单独的点赞表来记录用户点赞关系
        // 这里简化为直接更新点赞数，实际项目中需要检查用户是否已点赞
        
        // 先检查当前点赞数
        int currentLikes = commentMapper.getLikes(commentId);
        
        // 增加点赞数
        int result = commentMapper.updateLikes(commentId, 1);
        
        return result > 0;
    }
    
    @Override
    public int updateLikes(Integer commentId, Integer delta) {
        return commentMapper.updateLikes(commentId, delta);
    }
    
    @Override
    public int getLikes(Integer commentId) {
        return commentMapper.getLikes(commentId);
    }
    
    @Override
    public int addReply(CommentReply commentReply) {
        // 设置创建时间为当前时间
        commentReply.setCreateTime(new java.util.Date());
        return commentReplyMapper.insert(commentReply);
    }
    
    @Override
    public List<CommentReply> findRepliesByCommentId(Integer commentId) {
        return commentReplyMapper.findByCommentId(commentId);
    }
}