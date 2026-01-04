package nxu.service.impl;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * 数据库初始化器
 * 在应用启动时创建缺失的表
 */
@Service
public class DatabaseInitializer implements InitializingBean {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            System.out.println("开始初始化数据库表...");
            
            // 创建comment表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `comment` (" +
                "`id` INT AUTO_INCREMENT PRIMARY KEY, " +
                "`item_id` INT NOT NULL, " +
                "`user_id` INT NOT NULL, " +
                "`content` TEXT NOT NULL, " +
                "`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "`update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
            ")");
            
            // 创建favorite表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `favorite` (" +
                "`id` INT AUTO_INCREMENT PRIMARY KEY, " +
                "`user_id` INT NOT NULL, " +
                "`item_id` INT NOT NULL, " +
                "`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "UNIQUE KEY uk_user_item (`user_id`, `item_id`)" +
            ")");
            
            // 创建category表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `category` (" +
                "`id` INT AUTO_INCREMENT PRIMARY KEY, " +
                "`name` VARCHAR(100) NOT NULL, " +
                "`description` TEXT, " +
                "`icon` VARCHAR(100), " +
                "`sort_order` INT DEFAULT 0, " +
                "`status` TINYINT DEFAULT 1, " +
                "`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ")");
            
            // 插入默认分类数据
            try {
                jdbcTemplate.update("INSERT IGNORE INTO `category` (`name`, `description`, `sort_order`) VALUES " +
                    "('身份证', '身份证件类', 1), " +
                    "('钥匙', '钥匙类', 2), " +
                    "('手机', '手机类', 3), " +
                    "('钱包', '钱包类', 4), " +
                    "('书包', '书包类', 5), " +
                    "('学生证', '学生证类', 6), " +
                    "('耳机', '耳机类', 7), " +
                    "('充电器', '充电器类', 8), " +
                    "('书本', '书本类', 9), " +
                    "('文具', '文具类', 10), " +
                    "('其他', '其他物品', 99)");
            } catch (Exception e) {
                System.out.println("分类数据可能已存在: " + e.getMessage());
            }
            
            // 创建lost_item表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `lost_item` (" +
                "`id` INT AUTO_INCREMENT PRIMARY KEY, " +
                "`user_id` INT NOT NULL, " +
                "`category_id` INT NOT NULL, " +
                "`type` TINYINT NOT NULL, " +
                "`title` VARCHAR(200) NOT NULL, " +
                "`description` TEXT, " +
                "`lost_location` VARCHAR(200), " +
                "`lost_time` DATETIME, " +
                "`contact_info` VARCHAR(200), " +
                "`image_url` VARCHAR(500), " +
                "`status` TINYINT DEFAULT 0, " +
                "`view_count` INT DEFAULT 0, " +
                "`favorite_count` INT DEFAULT 0, " +
                "`comment_count` INT DEFAULT 0, " +
                "`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "`update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
            ")");
            
            // 创建user表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `user` (" +
                "`id` INT AUTO_INCREMENT PRIMARY KEY, " +
                "`username` VARCHAR(50) UNIQUE NOT NULL, " +
                "`password` VARCHAR(100) NOT NULL, " +
                "`real_name` VARCHAR(50), " +
                "`student_no` VARCHAR(20) UNIQUE, " +
                "`phone` VARCHAR(20), " +
                "`qq` VARCHAR(20), " +
                "`wechat` VARCHAR(50), " +
                "`email` VARCHAR(100), " +
                "`avatar` VARCHAR(500), " +
                "`status` TINYINT DEFAULT 1, " +
                "`last_login_time` DATETIME, " +
                "`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "`update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
            ")");
            
            System.out.println("数据库表初始化完成！");
            
        } catch (Exception e) {
            System.err.println("数据库初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}