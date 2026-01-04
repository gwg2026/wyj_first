package nxu.test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Comment Data Test Class
 */
public class CommentDataTest {
    
    public static void main(String[] args) {
        Connection conn = null;
        
        try {
            // Load database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Connect to database
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/ssm_lost_found?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC",
                "root", 
                "123456"
            );
            
            System.out.println("🔗 Database connected successfully");
            
            // Query existing comments
            List<CommentData> comments = getComments(conn);
            System.out.println("\n📝 Existing comment data (" + comments.size() + " comments):");
            for (CommentData comment : comments) {
                System.out.println("  Comment ID: " + comment.id + 
                                 ", Item ID: " + comment.itemId + 
                                 ", User ID: " + comment.userId + 
                                 ", Content: " + comment.content + 
                                 ", Likes: " + comment.likes + 
                                 ", Create Time: " + comment.createTime);
                
                // Query replies
                List<ReplyData> replies = getReplies(conn, comment.id);
                for (ReplyData reply : replies) {
                    System.out.println("    Reply ID: " + reply.id + 
                                     ", User ID: " + reply.userId + 
                                     ", Content: " + reply.content + 
                                     ", Create Time: " + reply.createTime);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Get comment list
     */
    private static List<CommentData> getComments(Connection conn) throws SQLException {
        List<CommentData> comments = new ArrayList<>();
        String sql = "SELECT id, item_id, user_id, content, likes, create_time FROM comment ORDER BY create_time DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                CommentData comment = new CommentData();
                comment.id = rs.getInt("id");
                comment.itemId = rs.getInt("item_id");
                comment.userId = rs.getInt("user_id");
                comment.content = rs.getString("content");
                comment.likes = rs.getInt("likes");
                comment.createTime = rs.getString("create_time");
                comments.add(comment);
            }
        }
        
        return comments;
    }
    
    /**
     * Get comment replies
     */
    private static List<ReplyData> getReplies(Connection conn, int commentId) throws SQLException {
        List<ReplyData> replies = new ArrayList<>();
        String sql = "SELECT id, user_id, content, create_time FROM comment_reply WHERE comment_id = ? ORDER BY create_time";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, commentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ReplyData reply = new ReplyData();
                    reply.id = rs.getInt("id");
                    reply.userId = rs.getInt("user_id");
                    reply.content = rs.getString("content");
                    reply.createTime = rs.getString("create_time");
                    replies.add(reply);
                }
            }
        }
        
        return replies;
    }
    
    /**
     * Comment data class
     */
    static class CommentData {
        int id;
        int itemId;
        int userId;
        String content;
        int likes;
        String createTime;
    }
    
    /**
     * Reply data class
     */
    static class ReplyData {
        int id;
        int userId;
        String content;
        String createTime;
    }
}