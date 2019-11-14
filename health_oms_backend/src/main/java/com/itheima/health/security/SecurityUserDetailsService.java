package com.itheima.health.security;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.pojo.Permission;
import com.itheima.health.pojo.Role;
import com.itheima.health.pojo.User;
import com.itheima.health.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
@Slf4j
public class SecurityUserDetailsService implements UserDetailsService {
    @Reference
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 模拟从数据库读取用户信息
        //User user = userDb.get(username);
        //rpc获取用户信息
        User user = userService.fndByUsername(username);

        if (null == user) {
            throw new UsernameNotFoundException("用户不存在");
        }

        String userName = user.getUsername();
        String password = user.getPassword();
        // 获取用户角色及权限
        Collection<GrantedAuthority> authList = new ArrayList<>();
        for (Role role : user.getRoles()) {
            // 角色关键词
            authList.add(new SimpleGrantedAuthority(role.getKeyword()));
            for (Permission permission : role.getPermissions()) {
                // 权限关键词
                authList.add(new SimpleGrantedAuthority(permission.getKeyword()));
            }
        }
		log.debug("[获取用户认证权限权信息]userName:{},authorities:{}",userName,authList);
        return new org.springframework.security.core.userdetails.User(userName, password, authList);
    }
}
