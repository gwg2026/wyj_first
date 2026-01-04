package nxu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 首页控制器
 * 处理根路径访问，重定向到登录页面
 */
@Controller
public class IndexController {

    /**
     * 访问根路径时重定向到失物列表
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "redirect:/lost/list";
    }
}

