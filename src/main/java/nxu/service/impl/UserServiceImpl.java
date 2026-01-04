package nxu.service.impl;

import nxu.dao.UserMapper;
import nxu.entity.User;
import nxu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 用户Service实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        // 直接使用明文密码查询数据库，与注册时的存储方式保持一致
        return userMapper.findByUsernameAndPassword(username, password);
    }

    @Override
    public int register(User user) {
        if (existsByUsername(user.getUsername())) {
            return 0; // 用户名已存在
        }
        return userMapper.insert(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userMapper.findByUsername(username) != null;
    }

    @Override
    public User findById(Integer id) {
        return userMapper.findById(id);
    }

    @Override
    public java.util.List<User> findAllUsers() {
        return userMapper.findAll();
    }

    /**
     * MD5加密方法
     * @param input 原始字符串
     * @return MD5加密后的字符串
     */
    private String md5Encrypt(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }
}

