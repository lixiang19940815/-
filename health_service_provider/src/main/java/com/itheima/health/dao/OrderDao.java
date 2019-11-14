package com.itheima.health.dao;

import com.itheima.health.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 预约DAO
 */
public interface OrderDao {

    /**
     * 根据条件查询
     * @param memberId  会员ID
     * @param setmealId 套餐ID
     * @param orderDate 预约日期
     * @return
     */

    /**
     * 根据会员、日期、套餐查询
     * @param memberId
     * @param orderDate
     * @param setmealId
     * @return
     */
    long countByMemberAndDateAndSetMeal(@Param("memberId") Integer memberId, @Param("orderDate") Date orderDate, @Param("setmealId") Integer setmealId);
    /**
     * 插入
     * @param order
     */
    void insert(Order order);

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    Order selectById(@Param("id") Integer id);

    /**
     * 根据套餐统计预约数量
     * 返回值格式：
     * [{"name":"套餐1","value":10}]
     * @return
     */
    List<Map<String, Object>> countBySetMeal();


    /**
     * 根据订单日期范围和订单状态统计
     * @param starDate
     * @param endDate
     * @param status
     * @return
     */
    Long countByOrderDateRange(@Param("starDate") Date starDate, @Param("endDate") Date endDate, @Param("status") String status);

    /**
     * 统计预约梳理最多的前topN个套现的信息
     * 返回数据格式:
     * [ {"proportion":0.4545,"name":"粉红珍爱(女)升级TM12项筛查体检套餐","setmeal_count":5}]
     * @param topN
     * @return
     */
    List<Map<String,Object>> countByHotSetMeal(@Param("topN") int topN);
}
