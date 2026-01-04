package nxu.dao;

import nxu.entity.User;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper接口
 */
public interface UserMapper {
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象
     */
    User findByUsername(@Param("username") String username);

    /**
     * 根据用户名和密码查询用户（用于登录验证）
     * @param username 用户名
     * @param password 密码
     * @return 用户对象
     */
    User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    /**
     * 注册用户
     * @param user 用户对象
     * @return 影响行数
     */
    int insert(User user);

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户对象
     */
    User findById(@Param("id") Integer id);

    /**
     * 获取所有用户（用于测试）
     * @return 用户列表
     */
    java.util.List<User> findAll();
}

