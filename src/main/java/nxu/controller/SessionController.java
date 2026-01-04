package nxu.controller;

import nxu.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 会话管理控制器，用于测试会话管理功能
 */
@Controller
public class SessionController {
    
    /**
     * 跳转到会话测试页面
     */
    @RequestMapping(value = "/session-test", method = RequestMethod.GET)
    public String sessionTestPage(HttpSession session, Model model) {
        if (session != null) {
            // 将会话信息添加到模型中，以便在模板中显示
            model.addAttribute("session", session);
            
            // 添加额外的会话统计信息
            model.addAttribute("sessionInfo", SessionUtil.getSessionInfo(session));
            
            // 直接添加会话属性到模型中，方便模板使用
            model.addAttribute("loginTime", session.getAttribute(SessionUtil.SESSION_LOGIN_TIME_KEY));
            model.addAttribute("loginIp", session.getAttribute(SessionUtil.SESSION_LOGIN_IP_KEY));
            model.addAttribute("deviceId", session.getAttribute(SessionUtil.SESSION_DEVICE_ID_KEY));
            model.addAttribute("rememberMe", session.getAttribute(SessionUtil.SESSION_REMEMBER_ME_KEY));
            
            // 添加格式化的日期字符串，避免模板中的日期计算问题
            if (session.getCreationTime() != 0) {
                model.addAttribute("creationTimeFormatted", 
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(session.getCreationTime())));
            } else {
                model.addAttribute("creationTimeFormatted", "未知");
            }
            
            if (session.getLastAccessedTime() != 0) {
                model.addAttribute("lastAccessedTimeFormatted", 
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(session.getLastAccessedTime())));
            } else {
                model.addAttribute("lastAccessedTimeFormatted", "暂无");
            }
            
            // 格式化登录时间
            Object loginTime = session.getAttribute(SessionUtil.SESSION_LOGIN_TIME_KEY);
            if (loginTime instanceof Long) {
                model.addAttribute("loginTimeFormatted", 
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date((Long) loginTime)));
            }
        }
        
        return "session-test";
    }
    
    /**
     * 获取会话统计信息（JSON格式）
     */
    @RequestMapping(value = "/session-stats", method = RequestMethod.GET)
    public String getSessionStats(HttpSession session, Model model) {
        if (session != null) {
            model.addAttribute("sessionInfo", SessionUtil.getSessionInfo(session));
            model.addAttribute("activeSessions", SessionUtil.getActiveSessions());
        }
        
        return "session-stats";
    }
}