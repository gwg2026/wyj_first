package nxu.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nxu.entity.User;
import nxu.util.SessionUtil;

import java.io.IOException;

/**
 * 会话过滤器，用于管理用户会话和更新访问时间
 */
@WebFilter(filterName = "SessionFilter", urlPatterns = {"/*"}, 
           dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE})
public class SessionFilter implements Filter {
    
    // 不需要过滤的URL模式
    private static final String[] EXCLUDED_URLS = {
        "/css/", "/js/", "/images/", "/uploads/", "/upload/",
        "/login", "/register", "/error"
    };
    
    // 需要登录才能访问的URL模式
    private static final String[] PROTECTED_URLS = {
        "/lost/add", "/lost/edit", "/lost/delete", 
        "/comment/add", "/comment/edit", "/comment/delete",
        "/user/profile", "/user/logout"
    };
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("SessionFilter初始化完成");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String relativePath = requestURI.substring(contextPath.length());
        
        // 检查是否需要过滤
        if (isExcludedUrl(relativePath)) {
            chain.doFilter(request, response);
            return;
        }
        
        // 处理会话逻辑
        if (session != null) {
            // 更新最后访问时间
            session.setAttribute(SessionUtil.SESSION_LAST_ACCESS_TIME_KEY, System.currentTimeMillis());
            
            // 获取当前用户
            User currentUser = SessionUtil.getCurrentUser(session);
            
            // 检查记住我功能
            if (currentUser == null && SessionUtil.isRememberMeEnabled(session)) {
                // 如果启用了记住我功能但没有用户，尝试恢复会话
                String deviceId = (String) session.getAttribute(SessionUtil.SESSION_DEVICE_ID_KEY);
                if (deviceId != null) {
                    // 这里可以实现基于设备ID的自动登录逻辑
                    System.out.println("尝试基于设备ID恢复会话: " + deviceId);
                }
            }
            
            // 检查是否是受保护的URL
            if (isProtectedUrl(relativePath) && currentUser == null) {
                // 保存当前URL以便登录后重定向
                session.setAttribute("returnUrl", relativePath);
                
                // 如果是AJAX请求，返回JSON响应
                if (isAjaxRequest(httpRequest)) {
                    httpResponse.setContentType("application/json;charset=UTF-8");
                    httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    httpResponse.getWriter().write("{\"error\":\"未登录\",\"code\":401,\"loginUrl\":\"/login\"}");
                    return;
                } else {
                    // 普通请求重定向到登录页面
                    httpResponse.sendRedirect(contextPath + "/login");
                    return;
                }
            }
            
            // 检查会话是否即将过期，如果是则刷新会话
            Long lastAccessTime = (Long) session.getAttribute(SessionUtil.SESSION_LAST_ACCESS_TIME_KEY);
            if (lastAccessTime != null) {
                long currentTime = System.currentTimeMillis();
                long timeSinceLastAccess = currentTime - lastAccessTime;
                
                // 如果距离上次访问超过15分钟，刷新会话超时时间
                if (timeSinceLastAccess > 15 * 60 * 1000) {
                    int maxInactiveInterval = SessionUtil.isRememberMeEnabled(session) ? 
                        SessionUtil.REMEMBER_ME_TIMEOUT : SessionUtil.NORMAL_SESSION_TIMEOUT;
                    session.setMaxInactiveInterval(maxInactiveInterval);
                    System.out.println("刷新会话超时时间 - 会话ID: " + session.getId() + ", 用户: " + 
                                     (currentUser != null ? currentUser.getUsername() : "匿名"));
                }
            }
            
        } else if (isProtectedUrl(relativePath)) {
            // 没有会话且访问受保护资源
            if (isAjaxRequest(httpRequest)) {
                httpResponse.setContentType("application/json;charset=UTF-8");
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write("{\"error\":\"未登录\",\"code\":401,\"loginUrl\":\"/login\"}");
                return;
            } else {
                httpResponse.sendRedirect(contextPath + "/login");
                return;
            }
        }
        
        // 继续处理请求
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        System.out.println("SessionFilter销毁");
    }
    
    /**
     * 检查URL是否是需要排除的URL
     */
    private boolean isExcludedUrl(String url) {
        for (String excludedUrl : EXCLUDED_URLS) {
            if (url.startsWith(excludedUrl)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 检查URL是否是需要保护的URL
     */
    private boolean isProtectedUrl(String url) {
        for (String protectedUrl : PROTECTED_URLS) {
            if (url.startsWith(protectedUrl)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 检查是否是AJAX请求
     */
    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestedWith);
    }
}