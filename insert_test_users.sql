-- 插入更多测试用户数据（使用常见用户名）
INSERT INTO `user` (`username`, `password`, `real_name`, `student_no`, `phone`, `qq`, `email`) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', 'admin', '13800138000', '888888888', 'admin@example.com'),
('user1', 'e10adc3949ba59abbe56e057f20f883e', '用户一', '2021004', '13800138004', '444555666', 'user1@example.com'),
('user2', 'e10adc3949ba59abbe56e057f20f883e', '用户二', '2021005', '13800138005', '777888999', 'user2@example.com'),
('test', 'e10adc3949ba59abbe56e057f20f883e', '测试用户', '2021006', '13800138006', '666777888', 'test@example.com'),
('zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '张三', '2021007', '13800138007', '555666777', 'zhangsan@student.edu.cn'),
('lisi', 'e10adc3949ba59abbe56e057f20f883e', '李四', '2021008', '13800138008', '444555666', 'lisi@student.edu.cn');

-- 查看当前所有用户
SELECT id, username, real_name, student_no, phone FROM `user`;