package com.itheima.health.service;

import com.itheima.health.exception.DuplicateOperateException;
import com.itheima.health.pojo.Member;

import java.util.List;

/**
 * 会员Service
 */
public interface MemberService {
    /**
     * 添加会员
     * @param member
     * @return
     * @throws DuplicateOperateException 用户手机号已经存在
     */
    Member addMember(Member member)throws DuplicateOperateException;

    /**
     * 根据手机号查询
     * @param phoneNumber
     * @return
     */
    Member findByPhone(String phoneNumber);

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    Member findById(Integer id);

    /**
     * 根据月份统计用户数量
     * @param months
     * @return
     */
    List<Integer> countByMonth(List<String> months);
}
