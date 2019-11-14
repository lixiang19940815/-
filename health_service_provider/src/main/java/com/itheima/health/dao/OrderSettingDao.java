package com.itheima.health.dao;

import com.itheima.health.pojo.OrderSetting;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author zhangmeng
 * @description 预约设置DAO
 * @date 2019/9/29
 **/
public interface OrderSettingDao {
    /**
     * 根据时间查询
     * @param orderDate
     * @return
     */
    OrderSetting selectByOrderDate(@Param("orderDate") Date orderDate);

    /**
     * 根据ID更新NUmber
     * @param id
     * @param number
     */
    void updateNumberById(@Param("id") Integer id, @Param("number") int number);

    /**
     * 插入
     * @param orderSetting
     */
    void insert(OrderSetting orderSetting);

    /**
     * 根据日期范围查询
     * @param startDate
     * @param endDate
     * @return
     */
    List<OrderSetting> selectByOrderDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 根据id更新已预约人数
     * @param id
     * @param reservations
     */
    void updateReservationsById(@Param("id") Integer id, @Param("reservations") int reservations);
}
