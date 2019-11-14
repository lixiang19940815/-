package com.itheima.health.service;

import com.itheima.health.exception.DuplicateOperateException;
import com.itheima.health.exception.OrderCountOverLimitExceptoin;
import com.itheima.health.exception.OrderTimeNotAllowedException;
import com.itheima.health.pojo.Order;

import java.util.List;
import java.util.Map;

/**
 * 预约Service
 */
public interface OrderService {
    /**
     * 添加预约
     *
     * @param order
     * @return
     * @throws OrderCountOverLimitExceptoin 预约满了
     * @throws DuplicateOperateException    同一个同一天同一个套餐（重复预约）
     * @throws OrderTimeNotAllowedException 套餐未设置（不允许预约）
     */
    Order add(Order order) throws OrderCountOverLimitExceptoin, DuplicateOperateException, OrderTimeNotAllowedException;

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    Order findById(Integer id);

    /**
     * 根据套餐统计预约数量
     * 返回数据格式：
     * [{"name":"套餐1","value":10}]
     * @return
     */
    List<Map<String, Object>> countBySetmeal();
}
