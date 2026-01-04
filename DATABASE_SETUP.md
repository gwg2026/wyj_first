# 数据库部署指南

## 概述
本项目使用 MySQL 数据库，包含完整的失物招领系统数据表结构和测试数据。

## 数据库配置

### 1. 数据库要求
- MySQL 5.7+ 或 MariaDB 10.3+
- 字符集：UTF8MB4
- 排序规则：utf8mb4_general_ci

### 2. 数据库创建步骤

#### 方式一：使用 MySQL 客户端
```bash
# 登录 MySQL
mysql -u root -p

# 执行数据库初始化脚本
source database_init.sql
source insert_test_users.sql
```

#### 方式二：使用命令行
```bash
# 直接执行 SQL 文件
mysql -u root -p < database_init.sql
mysql -u root -p < insert_test_users.sql
```

#### 方式三：导入到 MySQL Workbench 或其他 GUI 工具
1. 打开 MySQL Workbench
2. 连接到您的 MySQL 服务器
3. 依次打开并执行 `database_init.sql` 和 `insert_test_users.sql`

### 3. 数据库配置信息

数据库配置文件：`src/main/resources/database.properties`

```properties
# 数据库连接配置
jdbc.driver=com.mysql.cj.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/ssm_new_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false
jdbc.username=root
jdbc.password=your_password

# 连接池配置
jdbc.maxActive=20
jdbc.maxIdle=10
jdbc.minIdle=5
jdbc.initialSize=5
```

### 4. 测试账户

#### 管理员账户
- **用户名**: `admin`
- **密码**: `123456`
- **邮箱**: `admin@example.com`

#### 测试用户账户
- **用户名**: `user1`, `user2`, `test`, `zhangsan`, `lisi`
- **密码**: `123456` (所有测试用户密码相同)

### 5. 数据表结构

#### 核心表
1. **`user`** - 用户表
   - 用户基本信息、联系方式
   - 支持学号、手机、QQ、微信等多种联系方式

2. **`category`** - 失物分类表
   - 电子产品、钱包证件、书籍文具、服装配饰、其他物品

3. **`lost_item`** - 失物信息表
   - 支持失物和拾物两种类型
   - 包含标题、描述、地点、时间等完整信息
   - 支持图片上传
   - 状态管理（未找到/已找到）

4. **`comment`** - 评论表
   - 对失物信息的评论
   - 支持点赞功能

5. **`comment_reply`** - 评论回复表
   - 对评论的回复功能

6. **`favorite`** - 收藏表
   - 用户收藏失物信息

### 6. 功能特性

- ✅ **完整的用户管理系统**
- ✅ **失物分类管理**
- ✅ **失物信息发布和管理**
- ✅ **评论和回复系统**
- ✅ **收藏功能**
- ✅ **外键约束保证数据完整性**
- ✅ **时间戳自动维护**
- ✅ **支持中文字符**
- ✅ **测试数据完备**

### 7. 注意事项

1. **字符编码**：确保数据库使用 UTF8MB4 字符集以支持完整的中文字符
2. **外键约束**：数据库启用了外键约束，删除用户时会级联删除相关数据
3. **密码加密**：用户密码使用 BCrypt 加密存储
4. **时间字段**：所有时间字段使用 DATETIME 类型，支持时区设置

### 8. 故障排除

#### 常见问题
1. **连接失败**：检查 MySQL 服务是否启动，端口号是否正确
2. **字符编码问题**：确保数据库和表都使用 UTF8MB4 字符集
3. **权限问题**：确保数据库用户有足够的权限创建和操作数据库

#### 错误排查
```sql
-- 检查数据库字符集
SHOW VARIABLES LIKE 'character_set%';

-- 检查表字符集
SHOW CREATE DATABASE ssm_new_db;

-- 检查用户权限
SHOW GRANTS FOR 'your_username'@'localhost';
```

### 9. 数据备份

#### 备份命令
```bash
# 完整备份
mysqldump -u root -p ssm_new_db > ssm_backup_$(date +%Y%m%d).sql

# 只备份结构
mysqldump -u root -p --no-data ssm_new_db > ssm_structure.sql

# 只备份数据
mysqldump -u root -p --no-create-info ssm_new_db > ssm_data.sql
```

#### 恢复命令
```bash
# 恢复完整备份
mysql -u root -p ssm_new_db < ssm_backup_20250104.sql
```

---

## 联系信息
如有问题，请查看项目 README 或联系开发团队。