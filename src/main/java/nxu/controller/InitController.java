package nxu.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;

/**
 * 初始化检查Controller
 */
@Controller
public class InitController implements ServletContextAware {
    
    private ServletContext servletContext;
    
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
    
    @RequestMapping("/init-check")
    @ResponseBody
    public String initCheck(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== SpringMVC 初始化检查 ===<br>");
        sb.append("ServletContext: ").append(servletContext != null ? "OK" : "NULL").append("<br>");
        sb.append("Request URI: ").append(request.getRequestURI()).append("<br>");
        sb.append("Context Path: ").append(request.getContextPath()).append("<br>");
        sb.append("Dispatcher Type: ").append(request.getDispatcherType()).append("<br>");
        return sb.toString();
    }
}

