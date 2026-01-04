package nxu.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import nxu.entity.User;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 会话管理工具类，提供会话相关的工具方法
 */
public class SessionUtil {
    
    // 会话属性键
    public static final String SESSION_USER_KEY = "user";
    public static final String SESSION_LOGIN_TIME_KEY = "loginTime";
    public static final String SESSION_LAST_ACCESS_TIME_KEY = "lastAccessTime";
    public static final String SESSION_LOGIN_IP_KEY = "loginIp";
    public static final String SESSION_REMEMBER_ME_KEY = "rememberMe";
    public static final String SESSION_DEVICE_ID_KEY = "deviceId";
    
    // 记住我功能的超时时间（7天）
    public static final int REMEMBER_ME_TIMEOUT = 7 * 24 * 60 * 60; // 7天
    
    // 普通会话超时时间（30分钟）
    public static final int NORMAL_SESSION_TIMEOUT = 30 * 60; // 30分钟
    
    /**
     * 获取当前登录用户
     */
    public static User getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute(SESSION_USER_KEY);
    }
    
    /**
     * 设置当前登录用户
     */
    public static void setCurrentUser(HttpSession session, User user, HttpServletRequest request) {
        if (session == null || user == null) {
            return;
        }
        
        // 设置用户信息
        session.setAttribute(SESSION_USER_KEY, user);
        
        // 设置登录时间
        session.setAttribute(SESSION_LOGIN_TIME_KEY, System.currentTimeMillis());
        
        // 设置最后访问时间
        session.setAttribute(SESSION_LAST_ACCESS_TIME_KEY, System.currentTimeMillis());
        
        // 设置登录IP
        session.setAttribute(SESSION_LOGIN_IP_KEY, getClientIp(request));
        
        // 生成设备ID（用于记住我功能）
        String deviceId = UUID.randomUUID().toString();
        session.setAttribute(SESSION_DEVICE_ID_KEY, deviceId);
        
        System.out.println("用户登录成功 - 用户ID: " + user.getId() + ", 用户名: " + user.getUsername() + ", 设备ID: " + deviceId);
    }
    
    /**
     * 更新最后访问时间
     */
    public static void updateLastAccessTime(HttpSession session) {
        if (session != null) {
            session.setAttribute(SESSION_LAST_ACCESS_TIME_KEY, System.currentTimeMillis());
        }
    }
    
    /**
     * 获取用户在线时长（分钟）
     */
    public static long getOnlineMinutes(HttpSession session) {
        if (session == null) {
            return 0;
        }
        
        Long loginTime = (Long) session.getAttribute(SESSION_LOGIN_TIME_KEY);
        if (loginTime == null) {
            return 0;
        }
        
        long currentTime = System.currentTimeMillis();
        long onlineTime = currentTime - loginTime;
        return onlineTime / (1000 * 60); // 转换为分钟
    }
    
    /**
     * 获取会话详细信息
     */
    public static Map<String, Object> getSessionInfo(HttpSession session) {
        Map<String, Object> sessionInfo = new HashMap<>();
        
        if (session == null) {
            return sessionInfo;
        }
        
        User user = getCurrentUser(session);
        if (user != null) {
            sessionInfo.put("userId", user.getId());
            sessionInfo.put("username", user.getUsername());
            sessionInfo.put("realName", user.getRealName());
        }
        
        Long loginTime = (Long) session.getAttribute(SESSION_LOGIN_TIME_KEY);
        if (loginTime != null) {
            sessionInfo.put("loginTime", loginTime);
            sessionInfo.put("onlineMinutes", getOnlineMinutes(session));
        }
        
        String loginIp = (String) session.getAttribute(SESSION_LOGIN_IP_KEY);
        if (loginIp != null) {
            sessionInfo.put("loginIp", loginIp);
        }
        
        String deviceId = (String) session.getAttribute(SESSION_DEVICE_ID_KEY);
        if (deviceId != null) {
            sessionInfo.put("deviceId", deviceId);
        }
        
        sessionInfo.put("sessionId", session.getId());
        sessionInfo.put("maxInactiveInterval", session.getMaxInactiveInterval());
        sessionInfo.put("creationTime", session.getCreationTime());
        
        return sessionInfo;
    }
    
    /**
     * 启用记住我功能
     */
    public static void enableRememberMe(HttpSession session) {
        if (session != null) {
            session.setAttribute(SESSION_REMEMBER_ME_KEY, true);
            session.setMaxInactiveInterval(REMEMBER_ME_TIMEOUT);
            System.out.println("记住我功能已启用，会话超时时间: " + REMEMBER_ME_TIMEOUT + "秒");
        }
    }
    
    /**
     * 禁用记住我功能
     */
    public static void disableRememberMe(HttpSession session) {
        if (session != null) {
            session.removeAttribute(SESSION_REMEMBER_ME_KEY);
            session.setMaxInactiveInterval(NORMAL_SESSION_TIMEOUT);
            System.out.println("记住我功能已禁用，会话超时时间: " + NORMAL_SESSION_TIMEOUT + "秒");
        }
    }
    
    /**
     * 检查是否启用了记住我功能
     */
    public static boolean isRememberMeEnabled(HttpSession session) {
        if (session == null) {
            return false;
        }
        Boolean rememberMe = (Boolean) session.getAttribute(SESSION_REMEMBER_ME_KEY);
        return rememberMe != null && rememberMe;
    }
    
    /**
     * 检查用户是否已登录
     */
    public static boolean isUserLoggedIn(HttpSession session) {
        return getCurrentUser(session) != null;
    }
    
    /**
     * 清除用户会话
     */
    public static void clearUserSession(HttpSession session) {
        if (session != null) {
            User user = getCurrentUser(session);
            if (user != null) {
                System.out.println("用户会话清除 - 用户ID: " + user.getId() + ", 用户名: " + user.getUsername());
            }
            
            session.removeAttribute(SESSION_USER_KEY);
            session.removeAttribute(SESSION_LOGIN_TIME_KEY);
            session.removeAttribute(SESSION_LAST_ACCESS_TIME_KEY);
            session.removeAttribute(SESSION_LOGIN_IP_KEY);
            session.removeAttribute(SESSION_REMEMBER_ME_KEY);
            session.removeAttribute(SESSION_DEVICE_ID_KEY);
        }
    }
    
    /**
     * 获取客户端IP地址
     */
    private static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 对于通过多个代理的情况，第一个IP为客户端真实IP，多个IP按照','分割
        if (ip != null && ip.contains(",")) {
            ip = ip.substring(0, ip.indexOf(",")).trim();
        }
        
        return ip;
    }
    
    /**
     * 获取当前活跃会话数量
     */
    public static int getActiveSessionCount() {
        try {
            // 使用反射获取SessionListener中的活跃会话数
            Class<?> sessionListenerClass = Class.forName("nxu.listener.SessionListener");
            java.lang.reflect.Method method = sessionListenerClass.getMethod("getActiveSessionCount");
            return (Integer) method.invoke(null);
        } catch (Exception e) {
            System.out.println("无法获取活跃会话数量: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * 获取所有活跃会话信息
     */
    public static Map<String, Object> getActiveSessions() {
        Map<String, Object> sessionsInfo = new HashMap<>();
        try {
            // 使用反射获取SessionListener中的活跃会话
            Class<?> sessionListenerClass = Class.forName("nxu.listener.SessionListener");
            java.lang.reflect.Method method = sessionListenerClass.getMethod("getActiveSessions");
            Object activeSessions = method.invoke(null);
            
            if (activeSessions instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> activeSessionsMap = (Map<String, Object>) activeSessions;
                
                // 提取会话ID和相关信息（不包含敏感信息）
                Map<String, Object> safeSessions = new HashMap<>();
                for (Map.Entry<String, Object> entry : activeSessionsMap.entrySet()) {
                    String sessionId = entry.getKey();
                    Object session = entry.getValue();
                    
                    // 创建安全的会话信息映射
                    Map<String, Object> sessionInfo = new HashMap<>();
                    sessionInfo.put("sessionId", sessionId);
                    
                    // 使用反射获取会话信息
                    if (session instanceof jakarta.servlet.http.HttpSession) {
                        jakarta.servlet.http.HttpSession httpSession = (jakarta.servlet.http.HttpSession) session;
                        sessionInfo.put("creationTime", httpSession.getCreationTime());
                        sessionInfo.put("lastAccessedTime", httpSession.getLastAccessedTime());
                        sessionInfo.put("maxInactiveInterval", httpSession.getMaxInactiveInterval());
                        
                        // 添加用户信息（如果存在）
                        Object user = httpSession.getAttribute("user");
                        if (user != null) {
                            Map<String, Object> userInfo = new HashMap<>();
                            if (user instanceof nxu.entity.User) {
                                nxu.entity.User u = (nxu.entity.User) user;
                                userInfo.put("username", u.getUsername());
                                userInfo.put("id", u.getId());
                            }
                            sessionInfo.put("user", userInfo);
                        }
                    }
                    
                    safeSessions.put(sessionId, sessionInfo);
                }
                
                sessionsInfo.put("count", safeSessions.size());
                sessionsInfo.put("sessions", safeSessions);
            }
        } catch (Exception e) {
            System.out.println("无法获取活跃会话信息: " + e.getMessage());
            sessionsInfo.put("count", 0);
            sessionsInfo.put("error", "无法获取活跃会话信息");
        }
        
        return sessionsInfo;
    }
}