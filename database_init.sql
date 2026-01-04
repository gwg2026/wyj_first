-- 创建数据库
CREATE DATABASE IF NOT EXISTS ssm_new_db CHARACTER SET utf8 COLLATE utf8_general_ci;
USE ssm_new_db;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `real_name` VARCHAR(50) COMMENT '真实姓名',
  `student_no` VARCHAR(20) COMMENT '学号',
  `phone` VARCHAR(20) COMMENT '手机号',
  `qq` VARCHAR(20) COMMENT 'QQ号',
  `wechat` VARCHAR(50) COMMENT '微信号',
  `email` VARCHAR(100) COMMENT '邮箱',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 失物分类表
CREATE TABLE IF NOT EXISTS `category` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `description` VARCHAR(200) COMMENT '分类描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='失物分类表';

-- 失物信息表
CREATE TABLE IF NOT EXISTS `lost_item` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL COMMENT '发布用户ID',
  `category_id` INT(11) NOT NULL COMMENT '分类ID',
  `type` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '类型：1-失物，2-拾物',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `description` TEXT COMMENT '详细描述',
  `lost_location` VARCHAR(200) COMMENT '丢失/拾取地点',
  `lost_time` DATETIME COMMENT '丢失/拾取时间',
  `contact_info` VARCHAR(200) COMMENT '联系方式',
  `image_url` VARCHAR(500) COMMENT '图片URL',
  `status` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '状态：0-未找到/未认领，1-已找到/已认领',
  `view_count` INT(11) DEFAULT 0 COMMENT '浏览次数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='失物信息表';

-- 评论表
CREATE TABLE IF NOT EXISTS `comment` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `item_id` INT(11) NOT NULL COMMENT '失物ID',
  `user_id` INT(11) NOT NULL COMMENT '评论用户ID',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `likes` INT(11) DEFAULT 0 COMMENT '点赞数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_item_id` (`item_id`),
  KEY `idx_user_id` (`user_id`),
  FOREIGN KEY (`item_id`) REFERENCES `lost_item` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 评论回复表
CREATE TABLE IF NOT EXISTS `comment_reply` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `comment_id` INT(11) NOT NULL COMMENT '评论ID',
  `user_id` INT(11) NOT NULL COMMENT '回复用户ID',
  `content` TEXT NOT NULL COMMENT '回复内容',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_comment_id` (`comment_id`),
  KEY `idx_user_id` (`user_id`),
  FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论回复表';

-- 收藏表
CREATE TABLE IF NOT EXISTS `favorite` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL COMMENT '用户ID',
  `item_id` INT(11) NOT NULL COMMENT '失物ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_item` (`user_id`, `item_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_item_id` (`item_id`),
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`item_id`) REFERENCES `lost_item` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 插入初始分类数据
INSERT INTO `category` (`name`, `description`) VALUES
('电子产品', '手机、电脑、充电器等电子设备'),
('钱包证件', '钱包、身份证、银行卡等证件类物品'),
('书籍文具', '教材、笔记本、笔等学习用品'),
('服装配饰', '衣服、帽子、包包等穿戴物品'),
('其他物品', '不属于上述分类的其他物品');

-- 创建一个测试用户（用户名：admin，密码：123456）
INSERT INTO `user` (`username`, `password`, `real_name`, `email`) VALUES
('admin', '$2a$10$7JB720yubVSOfvVWmxiF4uF4Y0Q9xJfGZ5J7v2w5zW8y0x9y8zA', '管理员', 'admin@example.com');