package nxu.controller;

import nxu.entity.Category;
import nxu.entity.Comment;
import nxu.entity.CommentReply;
import nxu.entity.Favorite;
import nxu.entity.LostItem;
import nxu.entity.User;
import nxu.service.CategoryService;
import nxu.service.CommentService;
import nxu.service.FavoriteService;
import nxu.service.LostItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 失物信息Controller
 */
@Controller
@RequestMapping("/lost")
public class LostItemController {

    @Autowired
    private LostItemService lostItemService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private FavoriteService favoriteService;

    // Jackson ObjectMapper for JSON processing
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 图片上传路径（相对于web根目录）
    private static final String UPLOAD_PATH = "/uploads/images/";

    /**
     * 失物信息列表（全部）
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        List<LostItem> items = lostItemService.findAll();
        List<Category> categories = categoryService.findAll();
        model.addAttribute("items", items);
        model.addAttribute("categories", categories);
        model.addAttribute("type", null);  // 全部
        return "lost/list";
    }

    /**
     * 失物信息列表（按类型：1-失物，2-拾物）
     */
    @RequestMapping(value = "/list/{type}", method = RequestMethod.GET)
    public String listByType(@PathVariable Integer type, Model model) {
        List<LostItem> items = lostItemService.findByType(type);
        List<Category> categories = categoryService.findAll();
        model.addAttribute("items", items);
        model.addAttribute("categories", categories);
        model.addAttribute("type", type);
        return "lost/list";
    }

    /**
     * 最新失物信息
     */
    @RequestMapping(value = "/latest", method = RequestMethod.GET)
    public String latest(Model model) {
        List<LostItem> items = lostItemService.findLatest(20);
        List<Category> categories = categoryService.findAll();
        model.addAttribute("items", items);
        model.addAttribute("categories", categories);
        model.addAttribute("sort", "latest");
        return "lost/list";
    }

    /**
     * 最热失物信息
     */
    @RequestMapping(value = "/hot", method = RequestMethod.GET)
    public String hot(Model model) {
        List<LostItem> items = lostItemService.findHot(20);
        List<Category> categories = categoryService.findAll();
        model.addAttribute("items", items);
        model.addAttribute("categories", categories);
        model.addAttribute("sort", "hot");
        return "lost/list";
    }

    /**
     * 搜索失物信息
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(@RequestParam(required = false) String keyword,
                        @RequestParam(required = false) Integer categoryId,
                        Model model) {
        List<LostItem> items = lostItemService.search(keyword, categoryId);
        List<Category> categories = categoryService.findAll();
        model.addAttribute("items", items);
        model.addAttribute("categories", categories);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        return "lost/list";
    }

    /**
     * 失物信息详情
     */
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public String detail(@PathVariable Integer id, Model model) {
        LostItem item = lostItemService.viewDetail(id);
        if (item == null) {
            return "redirect:/lost/list";
        }
        model.addAttribute("item", item);
        
        // 获取相关推荐（根据分类相同或类型相同的其他物品）
        List<LostItem> relatedItems = lostItemService.findRelated(id, item.getCategoryId(), item.getType());
        model.addAttribute("relatedItems", relatedItems);
        
        return "lost/detail";
    }

    /**
     * 跳转到发布页面
     */
    @RequestMapping(value = "/publish", method = RequestMethod.GET)
    public String publishPage(Model model, HttpSession session, HttpServletRequest request) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            // 保存原始请求URL到session
            String originalUrl = request.getRequestURI();
            String queryString = request.getQueryString();
            if (queryString != null) {
                originalUrl += "?" + queryString;
            }
            session.setAttribute("returnUrl", originalUrl);
            
            return "redirect:/user/login";
        }
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        return "lost/publish";
    }

    /**
     * 发布失物信息
     */
    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    public String publish(@RequestParam Integer categoryId,
                         @RequestParam Integer type,
                         @RequestParam String title,
                         @RequestParam(required = false) String description,
                         @RequestParam(required = false) String lostLocation,
                         @RequestParam(required = false) String lostTimeStr,
                         @RequestParam(required = false) String contactInfo,
                         @RequestParam(required = false) MultipartFile imageFile,
                         HttpSession session,
                         HttpServletRequest request,
                         Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            // 保存原始请求URL到session
            String originalUrl = request.getRequestURI();
            String queryString = request.getQueryString();
            if (queryString != null) {
                originalUrl += "?" + queryString;
            }
            session.setAttribute("returnUrl", originalUrl);
            
            return "redirect:/user/login";
        }

        // 验证必填参数
        if (categoryId == null || type == null || title == null || title.trim().isEmpty()) {
            model.addAttribute("error", "请填写必填信息");
            model.addAttribute("categories", categoryService.findAll());
            return "lost/publish";
        }

        try {
            LostItem item = new LostItem();
            item.setUserId(user.getId());
            item.setCategoryId(categoryId);
            item.setType(type);
            item.setTitle(title.trim());
            item.setDescription(description != null ? description.trim() : "");
            item.setLostLocation(lostLocation != null ? lostLocation.trim() : "");
            item.setContactInfo(contactInfo != null ? contactInfo.trim() : "");

            // 处理时间
            if (lostTimeStr != null && !lostTimeStr.isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                    item.setLostTime(sdf.parse(lostTimeStr));
                } catch (Exception e) {
                    // 解析失败，使用当前时间
                    item.setLostTime(new Date());
                }
            } else {
                // 如果没有提供时间，使用当前时间
                item.setLostTime(new Date());
            }

            // 处理图片上传
            if (imageFile != null && !imageFile.isEmpty()) {
                // 检查文件大小（5MB）
                if (imageFile.getSize() > 5 * 1024 * 1024) {
                    model.addAttribute("error", "图片大小不能超过5MB");
                    model.addAttribute("categories", categoryService.findAll());
                    return "lost/publish";
                }
                
                String imageUrl = uploadImage(imageFile, session);
                item.setImageUrl(imageUrl);
            }

            // 发布失物信息
            int result = lostItemService.publish(item);
            if (result > 0) {
                // 发布成功，重定向到失物信息列表页面
                return "redirect:/lost/list";
            } else {
                model.addAttribute("error", "发布失败，请重试");
                model.addAttribute("categories", categoryService.findAll());
                return "lost/publish";
            }
        } catch (Exception e) {
            // 记录异常
            e.printStackTrace();
            model.addAttribute("error", "发布过程中发生错误：" + e.getMessage());
            model.addAttribute("categories", categoryService.findAll());
            return "lost/publish";
        }
    }

    /**
     * 更新状态
     */
    @RequestMapping(value = "/status/{id}", method = RequestMethod.POST)
    @ResponseBody
    public String updateStatus(@PathVariable Integer id, @RequestParam Integer status, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "{\"success\":false,\"message\":\"请先登录\"}";
        }

        LostItem item = lostItemService.findById(id);
        if (item == null || !item.getUserId().equals(user.getId())) {
            return "{\"success\":false,\"message\":\"无权限操作\"}";
        }

    lostItemService.updateStatus(id, status);
        return "{\"success\":true,\"message\":\"更新成功\"}";
    }

    /**
     * 我的发布
     */
    @RequestMapping(value = "/my", method = RequestMethod.GET)
    public String my(Model model, HttpSession session, HttpServletRequest request,
                    @RequestParam(required = false) String error) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            // 保存原始请求URL到session
            String originalUrl = request.getRequestURI();
            String queryString = request.getQueryString();
            if (queryString != null) {
                originalUrl += "?" + queryString;
            }
            session.setAttribute("returnUrl", originalUrl);
            
            model.addAttribute("needLogin", true);
            model.addAttribute("loginUrl", "/user/login");
            model.addAttribute("items", null);
            model.addAttribute("categories", categoryService.findAll());
            return "lost/my";
        }
        
        List<LostItem> items = lostItemService.findByUserId(user.getId());
        List<Category> categories = categoryService.findAll();
        model.addAttribute("items", items);
        model.addAttribute("categories", categories);
        model.addAttribute("user", user);
        model.addAttribute("needLogin", false);
        
        // 如果有错误信息，添加到model中
        if (error != null) {
            model.addAttribute("error", error);
        }
        
        return "lost/my";
    }

    /**
     * 删除失物信息
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public String deleteItem(@PathVariable Integer id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "{\"success\":false,\"message\":\"请先登录\"}";
        }

        LostItem item = lostItemService.findById(id);
        if (item == null || !item.getUserId().equals(user.getId())) {
            return "{\"success\":false,\"message\":\"无权限删除\"}";

        }

        lostItemService.deleteById(id);
        return "{\"success\":true,\"message\":\"删除成功\"}";
    }

    /**
     * 举报失物信息
     */
    @RequestMapping(value = "/report", method = RequestMethod.POST)
    @ResponseBody
    public String reportItem(@RequestParam Integer itemId, @RequestParam String reason, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "{\"success\":false,\"message\":\"请先登录\"}";
        }

        // 这里可以实现举报逻辑，比如保存到举报表
        // 目前简单返回成功
        System.out.println("举报信息: itemId=" + itemId + ", reason=" + reason + ", userId=" + user.getId());
        
        return "{\"success\":true,\"message\":\"举报已提交\"}";
    }

    /**
     * 获取热门标签
     */
    @RequestMapping(value = "/hot-tags", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getHotTags() {
        // 模拟热门标签数据
        return java.util.Arrays.asList("身份证", "钥匙", "手机", "钱包", "书包", "学生证", "耳机", "充电器");
    }

    /**
     * 获取统计信息
     */
    @RequestMapping(value = "/stats", method = RequestMethod.GET)
    @ResponseBody
    public java.util.Map<String, Object> getStats() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalItems", lostItemService.findAll().size());
        stats.put("resolvedItems", lostItemService.findByStatus(1).size());
        stats.put("lostItems", lostItemService.findByType(1).size());
        stats.put("foundItems", lostItemService.findByType(2).size());
        return stats;
    }

    /**
     * 上传图片
     */
    private String uploadImage(MultipartFile file, HttpSession session) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // 获取文件扩展名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return null;
            }
            
            // 检查文件类型
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            if (!extension.matches("\\.(jpg|jpeg|png|gif)$")) {
                throw new IllegalArgumentException("只支持jpg、jpeg、png、gif格式的图片");
            }

            // 生成唯一文件名
            String filename = UUID.randomUUID().toString() + extension;

            // 获取上传目录的真实路径
            String realPath = session.getServletContext().getRealPath(UPLOAD_PATH);
            File uploadDir = new File(realPath);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    throw new IOException("创建上传目录失败");
                }
            }

            // 保存文件
            File destFile = new File(uploadDir, filename);
            file.transferTo(destFile);
            
            // 调试信息：打印文件保存路径
            System.out.println("图片已保存到: " + destFile.getAbsolutePath());
            System.out.println("图片访问路径: " + UPLOAD_PATH + filename);

            // 返回访问路径
            return UPLOAD_PATH + filename;
        } catch (IOException e) {
            throw new IOException("图片上传失败：" + e.getMessage(), e);
        } catch (Exception e) {
            throw new IOException("图片处理失败：" + e.getMessage(), e);
        }
    }

    /**
     * 切换收藏状态
     */
    @RequestMapping(value = "/favorite", method = RequestMethod.POST)
    @ResponseBody
    public String toggleFavorite(@RequestParam Integer itemId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "{\"success\":false,\"message\":\"请先登录\"}";
        }

        Favorite favorite = favoriteService.toggleFavorite(user.getId(), itemId);
        String message = favorite.isIsFavorited() ? "收藏成功" : "取消收藏成功";
        return String.format("{\"success\":true,\"message\":\"%s\",\"isFavorited\":%s}", 
                           message, favorite.isIsFavorited());
    }

    /**
     * 检查收藏状态
     */
    @RequestMapping(value = "/favorite/status/{itemId}", method = RequestMethod.GET)
    @ResponseBody
    public String checkFavoriteStatus(@PathVariable Integer itemId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "{\"success\":false,\"message\":\"请先登录\"}";
        }

        boolean isFavorited = favoriteService.isFavorited(user.getId(), itemId);
        return String.format("{\"success\":true,\"isFavorited\":%s}", isFavorited);
    }

    /**
     * 添加评论
     */
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    @ResponseBody
    public String addComment(@RequestParam Integer itemId, @RequestParam String content, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "{\"success\":false,\"message\":\"请先登录\"}";
        }

        if (content == null || content.trim().isEmpty()) {
            return "{\"success\":false,\"message\":\"评论内容不能为空\"}";
        }

        if (content.length() > 500) {
            return "{\"success\":false,\"message\":\"评论内容不能超过500字符\"}";
        }

        Comment comment = new Comment();
        comment.setItemId(itemId);
        comment.setUserId(user.getId());
        comment.setContent(content.trim());

        commentService.addComment(comment);
        return "{\"success\":true,\"message\":\"评论成功\"}";
    }

    /**
     * 获取评论列表
     */
    @RequestMapping(value = "/comments/{itemId}", method = RequestMethod.GET)
    @ResponseBody
    public Object getComments(@PathVariable Integer itemId) {
        try {
            List<Comment> comments = commentService.findByItemId(itemId);
            
            // 使用Jackson构建JSON响应
            ObjectNode response = objectMapper.createObjectNode();
            response.put("success", true);
            
            ArrayNode commentsArray = objectMapper.createArrayNode();
            for (Comment comment : comments) {
                ObjectNode commentNode = objectMapper.createObjectNode();
                commentNode.put("id", comment.getId());
                commentNode.put("content", comment.getContent() != null ? comment.getContent() : "");
                commentNode.put("userName", comment.getUser() != null ? comment.getUser().getRealName() : "匿名用户");
                commentNode.put("createTime", comment.getCreateTime() != null ? comment.getCreateTime().toString() : "");
                commentNode.put("likes", comment.getLikes() != null ? comment.getLikes() : 0);
                
                // 处理回复数据
                ArrayNode repliesArray = objectMapper.createArrayNode();
                if (comment.getReplies() != null) {
                    for (CommentReply reply : comment.getReplies()) {
                        ObjectNode replyNode = objectMapper.createObjectNode();
                        replyNode.put("id", reply.getId());
                        replyNode.put("content", reply.getContent() != null ? reply.getContent() : "");
                        replyNode.put("userName", reply.getUser() != null ? reply.getUser().getRealName() : "匿名用户");
                        replyNode.put("createTime", reply.getCreateTime() != null ? reply.getCreateTime().toString() : "");
                        repliesArray.add(replyNode);
                    }
                }
                commentNode.set("replies", repliesArray);
                commentsArray.add(commentNode);
            }
            
            response.set("comments", commentsArray);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            // 返回错误响应
            ObjectNode errorResponse = objectMapper.createObjectNode();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取评论失败: " + e.getMessage());
            return errorResponse;
        }
    }

    /**
     * 评论点赞
     */
    @RequestMapping(value = "/comment/like", method = RequestMethod.POST)
    @ResponseBody
    public String toggleCommentLike(@RequestParam Integer commentId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "{\"success\":false,\"message\":\"请先登录\"}";
        }

        try {
            // 点赞评论
            boolean success = commentService.toggleLike(user.getId(), commentId);
            if (success) {
                // 获取最新点赞数
                int newLikeCount = commentService.getLikes(commentId);
                return String.format("{\"success\":true,\"message\":\"点赞成功\",\"likeCount\":%d,\"action\":\"like\"}", newLikeCount);
            } else {
                return "{\"success\":false,\"message\":\"点赞失败\"}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"success\":false,\"message\":\"点赞失败，请稍后重试\"}";
        }
    }

    /**
     * 评论回复
     */
    @RequestMapping(value = "/comment/reply", method = RequestMethod.POST)
    @ResponseBody
    public String replyToComment(@RequestParam Integer commentId, 
                                @RequestParam String content, 
                                HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "{\"success\":false,\"message\":\"请先登录\"}";
        }

        if (content == null || content.trim().isEmpty()) {
            return "{\"success\":false,\"message\":\"回复内容不能为空\"}";
        }

        if (content.length() > 500) {
            return "{\"success\":false,\"message\":\"回复内容不能超过500字符\"}";
        }

        try {
            // 创建回复对象
            CommentReply commentReply = new CommentReply();
            commentReply.setCommentId(commentId);
            commentReply.setUserId(user.getId());
            commentReply.setContent(content.trim());
            
            // 保存回复到数据库
            int result = commentService.addReply(commentReply);
            
            if (result > 0) {
                // 获取该评论的所有回复（用于前端显示更新）
                List<CommentReply> replies = commentService.findRepliesByCommentId(commentId);
                
                // 使用Jackson构建JSON响应
                ObjectNode response = objectMapper.createObjectNode();
                response.put("success", true);
                response.put("message", "回复成功");
                response.put("replyCount", replies.size());
                
                // 构建回复HTML数组
                ArrayNode replyHtmlArray = objectMapper.createArrayNode();
                for (CommentReply reply : replies) {
                    String username = (reply.getUser() != null && reply.getUser().getUsername() != null) 
                        ? reply.getUser().getUsername() : "匿名用户";
                    
                    StringBuilder replyHtml = new StringBuilder();
                    replyHtml.append("<div class='reply-item' style='margin-left: 20px; padding: 5px; border-left: 2px solid #eee;'>")
                            .append("<div class='reply-header' style='font-size: 12px; color: #666; margin-bottom: 3px;'>")
                            .append("<span class='reply-username'>").append(username).append("</span>")
                            .append("<span class='reply-time' style='margin-left: 10px;'>")
                            .append(reply.getCreateTime()).append("</span>")
                            .append("</div>")
                            .append("<div class='reply-content' style='font-size: 14px; line-height: 1.4;'>")
                            .append(escapeHtml(reply.getContent())).append("</div>")
                            .append("</div>");
                    
                    replyHtmlArray.add(replyHtml.toString());
                }
                
                response.set("replyHtml", replyHtmlArray);
                return objectMapper.writeValueAsString(response);
            } else {
                ObjectNode errorResponse = objectMapper.createObjectNode();
                errorResponse.put("success", false);
                errorResponse.put("message", "回复失败");
                return objectMapper.writeValueAsString(errorResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ObjectNode errorResponse = objectMapper.createObjectNode();
            errorResponse.put("success", false);
            errorResponse.put("message", "回复失败，请稍后重试");
            try {
                return objectMapper.writeValueAsString(errorResponse);
            } catch (Exception jsonError) {
                return "{\"success\":false,\"message\":\"回复失败，请稍后重试\"}";
            }
        }
    }

    /**
     * 转义HTML字符串
     */
    private String escapeHtml(String str) {
        if (str == null) return "";
        return str.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#x27;");
    }

    /**
     * 转义JSON字符串
     */
    private String escapeJson(String str) {
        if (str == null) return "";
        StringBuilder result = new StringBuilder();
        for (char c : str.toCharArray()) {
            switch (c) {
                case '"':
                    result.append("\\\"");
                    break;
                case '\\':
                    result.append("\\\\");
                    break;
                case '\b':
                    result.append("\\b");
                    break;
                case '\f':
                    result.append("\\f");
                    break;
                case '\n':
                    result.append("\\n");
                    break;
                case '\r':
                    result.append("\\r");
                    break;
                case '\t':
                    result.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        // 控制字符使用Unicode转义
                        result.append(String.format("\\u%04x", (int) c));
                    } else {
                        result.append(c);
                    }
            }
        }
        return result.toString();
    }
}

