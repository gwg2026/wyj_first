package nxu.service;

import nxu.entity.User;

/**
 * 用户Service接口
 */
public interface UserService {
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码（明文，在Service层进行加密处理）
     * @return 用户对象，如果登录失败返回null
     */
    User login(String username, String password);

    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册成功返回用户ID，失败返回0
     */
    int register(User user);

    /**
     * 根据用户名检查用户是否存在
     * @param username 用户名
     * @return 存在返回true，不存在返回false
     */
    boolean existsByUsername(String username);

    /**
     * 根据ID获取用户信息
     * @param id 用户ID
     * @return 用户对象
     */
    User findById(Integer id);

    /**
     * 获取所有用户（用于测试）
     * @return 用户列表
     */
    java.util.List<User> findAllUsers();
}

