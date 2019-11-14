package com.itheima.health.dao;

import com.itheima.health.pojo.Member;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * 会员DAO
 */
public interface MemberDao {

    /**
     * 插入
     * @param member
     */
    void insert(Member member);

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    Member selectById(@Param("id") Integer id);

    /**
     * 根据手机号查询
     * @param phoneNumber
     * @return
     */
    Member selectPhoneNumber(@Param("phoneNumber") String phoneNumber);

    /**
     * 查询日期之前的用户数量
     * @param endDate
     * @return
     */
    Long countByRegTimeBefore(@Param("endDate") String endDate);

    /**
     * 根据注册日期范围统计用户数量
     * @param startDate
     * @param endDate
     * @return
     */
    Long countByRegTimeRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
