package nxu.controller;

import nxu.entity.User;
import nxu.service.UserService;
import nxu.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 用户Controller
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 跳转到登录页面
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage(HttpServletRequest request, HttpSession session) {
        // 保存当前请求的URL，用于登录成功后跳转
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            // 只保存相对路径，避免外部重定向攻击
            if (referer.startsWith(request.getContextPath())) {
                String returnUrl = referer.substring(request.getContextPath().length());
                session.setAttribute("returnUrl", returnUrl);
            }
        }
        return "login";
    }

    /**
     * 处理登录请求
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam String username,
                       @RequestParam String password,
                       @RequestParam(required = false) String rememberMe,
                       Model model,
                       HttpSession session,
                       HttpServletRequest request) {
        
        // 调用Service进行登录验证
        User user = userService.login(username, password);

        if (user != null) {
            // 登录成功，使用优化的会话管理
            SessionUtil.setCurrentUser(session, user, request);
            
            // 处理记住我功能
            if ("on".equals(rememberMe) || "true".equals(rememberMe)) {
                SessionUtil.enableRememberMe(session);
            } else {
                SessionUtil.disableRememberMe(session);
            }
            
            // 检查是否有保存的返回URL
            String returnUrl = (String) session.getAttribute("returnUrl");
            if (returnUrl != null && !returnUrl.isEmpty()) {
                // 验证返回URL的安全性，防止开放重定向漏洞
                if (returnUrl.startsWith("/") && !returnUrl.startsWith("//")) {
                    // 清除保存的返回URL
                    session.removeAttribute("returnUrl");
                    return "redirect:" + returnUrl;
                } else {
                    // 返回URL不安全，使用默认跳转
                    return "redirect:/lost/list";
                }
            } else {
                // 没有保存的返回URL，直接跳转到失物列表页面
                return "redirect:/lost/list";
            }
        } else {
            // 登录失败，返回错误信息
            model.addAttribute("error", "用户名或密码错误！");
            model.addAttribute("username", username); // 保留用户名供表单回填
            return "login";
        }
    }

    /**
     * 跳转到注册页面
     */
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerPage() {
        return "register";
    }

    /**
     * 处理注册请求
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String realName,
                          @RequestParam(required = false) String studentNo,
                          @RequestParam(required = false) String phone,
                          @RequestParam(required = false) String qq,
                          @RequestParam(required = false) String wechat,
                          @RequestParam(required = false) String email,
                          Model model) {
        
        // 简单验证
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty() ||
            realName == null || realName.trim().isEmpty()) {
            model.addAttribute("error", "用户名、密码和真实姓名不能为空！");
            return "register";
        }

        // 检查用户名是否已存在
        if (userService.existsByUsername(username.trim())) {
            model.addAttribute("error", "用户名已存在，请选择其他用户名！");
            return "register";
        }

        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(password.trim()); // 演示系统暂不加密
        user.setRealName(realName.trim());
        user.setStudentNo(studentNo);
        user.setPhone(phone);
        user.setQq(qq);
        user.setWechat(wechat);
        user.setEmail(email);

        int result = userService.register(user);
        if (result > 0) {
            model.addAttribute("success", "注册成功！请使用用户名和密码登录。");
            return "redirect:/user/login";
        } else {
            model.addAttribute("error", "注册失败，请稍后重试！");
            return "register";
        }
    }

    /**
     * 登录成功页面
     */
    @RequestMapping("/success")
    public String success(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("user", user);
        return "success";
    }

    /**
     * 退出登录
     */
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        // 使用SessionUtil清除用户会话信息
        SessionUtil.clearUserSession(session);
        session.invalidate();
        return "redirect:/user/login";
    }

    /**
     * 检查用户名是否存在（AJAX接口）
     */
    @RequestMapping(value = "/check-username", method = RequestMethod.GET)
    @ResponseBody
    public String checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return "{\"exists\":" + exists + "}";
    }
}

