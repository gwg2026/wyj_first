package nxu.listener;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话监听器，用于管理用户会话
 */
@WebListener
public class SessionListener implements HttpSessionListener {
    
    // 存储所有活跃的会话
    private static final ConcurrentHashMap<String, HttpSession> activeSessions = new ConcurrentHashMap<>();
    
    // 默认会话超时时间（30分钟）
    private static final int DEFAULT_SESSION_TIMEOUT = 30 * 60; // 30分钟
    
    @Override
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        
        // 设置会话超时时间
        session.setMaxInactiveInterval(DEFAULT_SESSION_TIMEOUT);
        
        // 存储会话
        activeSessions.put(session.getId(), session);
        
        System.out.println("会话创建: " + session.getId() + ", 当前活跃会话数: " + activeSessions.size());
    }
    
    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        
        // 移除会话
        activeSessions.remove(session.getId());
        
        System.out.println("会话销毁: " + session.getId() + ", 当前活跃会话数: " + activeSessions.size());
    }
    
    /**
     * 获取当前活跃会话数
     */
    public static int getActiveSessionCount() {
        return activeSessions.size();
    }
    
    /**
     * 根据会话ID获取会话
     */
    public static HttpSession getSession(String sessionId) {
        return activeSessions.get(sessionId);
    }
    
    /**
     * 获取所有活跃会话
     */
    public static ConcurrentHashMap<String, HttpSession> getActiveSessions() {
        return new ConcurrentHashMap<>(activeSessions);
    }
    
    /**
     * 使会话失效
     */
    public static void invalidateSession(String sessionId) {
        HttpSession session = activeSessions.get(sessionId);
        if (session != null) {
            session.invalidate();
            activeSessions.remove(sessionId);
        }
    }
}