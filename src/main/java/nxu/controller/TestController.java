package nxu.controller;

import nxu.entity.User;
import nxu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @Autowired
    private UserService userService;

    @RequestMapping("/test/login")
    @ResponseBody
    public String testLogin(String username, String password) {
        try {
            User user = userService.login(username, password);
            if (user != null) {
                return "登录成功！用户信息：" + user.toString();
            } else {
                return "登录失败！用户名或密码错误";
            }
        } catch (Exception e) {
            return "登录异常：" + e.getMessage();
        }
    }

    @RequestMapping("/test/users")
    @ResponseBody
    public String testUsers() {
        try {
            return "应用已启动，UserService注入成功";
        } catch (Exception e) {
            return "测试异常：" + e.getMessage();
        }
    }

    @RequestMapping("/test/user-list")
    @ResponseBody
    public String testUserList() {
        try {
            java.util.List<nxu.entity.User> users = userService.findAllUsers();
            if (users != null && !users.isEmpty()) {
                StringBuilder sb = new StringBuilder("数据库中的用户列表：\n");
                for (nxu.entity.User user : users) {
                    sb.append("ID: ").append(user.getId())
                      .append(", 用户名: ").append(user.getUsername())
                      .append(", 密码: ").append(user.getPassword())
                      .append(", 姓名: ").append(user.getRealName())
                      .append("\n");
                }
                return sb.toString();
            } else {
                return "数据库中没有找到用户数据";
            }
        } catch (Exception e) {
            return "查询异常：" + e.getMessage();
        }
    }

    @RequestMapping("/test/register")
    @ResponseBody
    public String testRegister(String username, String password, String realName) {
        try {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setRealName(realName);
            
            int result = userService.register(user);
            if (result > 0) {
                return "用户注册成功！用户名：" + username;
            } else {
                return "用户注册失败！";
            }
        } catch (Exception e) {
            return "注册异常：" + e.getMessage();
        }
    }
}