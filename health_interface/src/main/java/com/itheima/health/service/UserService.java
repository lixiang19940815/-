package com.itheima.health.service;

import com.itheima.health.pojo.User;

/**
 * @author zhangmeng
 * @description 用户服务接口
 * @date 2019/9/6
 **/
public interface UserService {

    /**
     * 用户名密码登录
     * @param userName 用户名
     * @param password 密码
     * @return 是否成功
     */
    boolean login(String userName,String password);

    /**
     * 根据用户名查询用户详细信息
     * @param username
     * @return
     */
    User fndByUsername(String username);
}
