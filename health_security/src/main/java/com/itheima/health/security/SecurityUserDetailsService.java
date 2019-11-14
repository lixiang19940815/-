package com.itheima.health.security;

import com.itheima.health.pojo.Permission;
import com.itheima.health.pojo.Role;
import com.itheima.health.pojo.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SecurityUserDetailsService  implements UserDetailsService {
	// 模拟数据库的用户记录，如下User类是health_common中的自定义实体类User
	// 修改Role、Permission，为其增加不带参、带参构造方法
	private static Map<String, User> userDb = new HashMap();
	static {
		BCryptPasswordEncoder bCryptPasswordEncoder=new BCryptPasswordEncoder();
		User user1 = new User();
		user1.setUsername("admin");
		user1.setPassword(bCryptPasswordEncoder.encode("123"));
		// 用户权限与角色
		Role role1 = new Role("系统管理员","ROLE_ADMIN");
		role1.getPermissions().add(new Permission("添加权限","add"));
		role1.getPermissions().add(new Permission("删除权限","delete"));
		role1.getPermissions().add(new Permission("更新权限","update"));
		role1.getPermissions().add(new Permission("查询权限","find"));
		user1.getRoles().add(role1);
		userDb.put(user1.getUsername(),user1);

		User userZhangSan = new User();
		userZhangSan.setUsername("zhangsan");
		userZhangSan.setPassword(bCryptPasswordEncoder.encode("123"));
		Role role2 = new Role("数据分析员","ROLE_READER");
		role2.getPermissions().add(new Permission("查询权限","find"));
		userZhangSan.getRoles().add(role2);
		userDb.put(userZhangSan.getUsername(),userZhangSan);

		User userLisi = new User();
		userLisi.setUsername("lisi");
		userLisi.setPassword(bCryptPasswordEncoder.encode("123"));
		Role role3 = new Role("运营管理员","ROLE_OMS");;
		role3.getPermissions().add(new Permission("添加权限","add"));
		role3.getPermissions().add(new Permission("更新权限","update"));
		userLisi.getRoles().add(role3);
		userDb.put(userLisi.getUsername(),userLisi);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// 模拟从数据库读取用户信息
		User user = userDb.get(username);
		if(null == user){
			throw new UsernameNotFoundException("用户不存在");
		}

		String userName = user.getUsername();
		String password = user.getPassword();
		// 获取用户角色及权限
		Collection<GrantedAuthority> authList = new ArrayList<>();
		for(Role role:user.getRoles()){
			// 角色关键词
			authList.add(new SimpleGrantedAuthority(role.getKeyword()));
			for (Permission permission:role.getPermissions()){
				// 权限关键词
				authList.add(new SimpleGrantedAuthority(permission.getKeyword()));
			}
		}

		return new org.springframework.security.core.userdetails.User(userName,password,authList);
	}
}
