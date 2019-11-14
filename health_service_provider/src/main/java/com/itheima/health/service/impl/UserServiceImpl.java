package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.PermissionDao;
import com.itheima.health.dao.RoleDao;
import com.itheima.health.dao.UserDao;
import com.itheima.health.pojo.Permission;
import com.itheima.health.pojo.Role;
import com.itheima.health.pojo.User;
import com.itheima.health.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author zhangmeng
 * @description 用户服务实现类
 * @date 2019/9/6
 **/
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private PermissionDao permissionDao;

    @Override
    public boolean login(String userName, String password) {
        log.info("[登录]u：{}，p:{}", userName, password);
        return userName.equals(userName) && "123".equals(password);
    }

    @Override
    public User fndByUsername(String username) {
        //先查基本信息
        User user = userDao.selectByUsername(username);
        if (null == user) {
            //用户不存在
            return null;
        }
        //根据用户ID查角色信息
        List<Role> roles = roleDao.selectByUserId(user.getId());
        for (Role role : roles) {
            //根据角色ID查权限信息
            List<Permission> permissions = permissionDao.selectByRoleId(role.getId());
            role.getPermissions().addAll(permissions);
        }
        user.getRoles().addAll(roles);
        return user;
    }
}
