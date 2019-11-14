package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.OrderDao;
import com.itheima.health.dao.OrderSettingDao;
import com.itheima.health.exception.DuplicateOperateException;
import com.itheima.health.exception.OrderCountOverLimitExceptoin;
import com.itheima.health.exception.OrderTimeNotAllowedException;
import com.itheima.health.pojo.Order;
import com.itheima.health.pojo.OrderSetting;
import com.itheima.health.service.OrderService;
import com.itheima.health.service.OrderSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 预约ServiceImpl
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderSettingDao orderSettingDao;
    @Autowired
    private OrderDao orderDao;
    @Transactional
    @Override
    public Order add(Order order) throws OrderCountOverLimitExceptoin, DuplicateOperateException, OrderTimeNotAllowedException {
        log.info("[预约]data:{}",order);

        //查询预约设置信息
        OrderSetting orderSetting = orderSettingDao.selectByOrderDate(order.getOrderDate());
        if (null == orderSetting){
            //如果不存在-不能预约
            throw new OrderTimeNotAllowedException("该时间不能预约");
        }else if(orderSetting.getReservations()>=orderSetting.getNumber()){
            //如果存在-是否约满
            throw new OrderCountOverLimitExceptoin("已约满");
        }

        //查询是否有重复预约  同一个人同一天同一个套餐
        long count = orderDao.countByMemberAndDateAndSetMeal(order.getMemberId(),order.getOrderDate(),order.getSetmealId());
        if(count>0){
            throw new DuplicateOperateException("重复预约");
        }

        //插入数据
        orderDao.insert(order);

        //修改已预约数
        orderSettingDao.updateReservationsById(orderSetting.getId(),orderSetting.getReservations()+1);

        return orderDao.selectById(order.getId());
    }

    @Override
    public Order findById(Integer id) {

        return orderDao.selectById(id);
    }

    @Override
    public List<Map<String, Object>> countBySetmeal() {
        return orderDao.countBySetMeal();
    }
}
