package com.itheima.health.dao;

import com.itheima.health.pojo.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色DAO
 */
public interface RoleDao {
    /**
     * 根据用户ID查询
     * @param userId
     * @return
     */
    List<Role> selectByUserId(@Param("userId") Integer userId);
}
