package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.OrderSettingDao;
import com.itheima.health.pojo.OrderSetting;
import com.itheima.health.service.OrderSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author zhangmeng
 * @description
 * @date 2019/9/29
 **/
@Service(interfaceClass = OrderSettingService.class)
@Slf4j
public class OrderSettingServiceImpl implements OrderSettingService {
    @Autowired
    private OrderSettingDao orderSettingDao;

    @Transactional
    @Override
    public void addAll(List<OrderSetting> orderSettings) {
        log.info("[预约设置-批量添加]data：{}", orderSettings);
        //循环操作
        for (OrderSetting orderSetting : orderSettings) {
            editNumberByDate(orderSetting.getOrderDate(), orderSetting.getNumber());
        }
    }

    @Override
    public List<OrderSetting> getOrderSettingByMonth(int year, int month) {

        //构造日期范围
        String startDate = String.format("%d-%d-1", year, month);
        String endDate = String.format("%d-%d-31", year, month);
        //DAO查询并返回
        return orderSettingDao.selectByOrderDateRange(startDate, endDate);
    }

    @Transactional
    @Override
    public void editNumberByDate(Date orderDate, int number) {
        log.info("[预约设置-根据日期编辑]date:{},number:{}", orderDate, number);
        // 判断是否已存在
        OrderSetting po = orderSettingDao.selectByOrderDate(orderDate);
        if (null != po) {
            // 存在则更新
            orderSettingDao.updateNumberById(po.getId(), number);
        } else {
            // 不存在则插入
            orderSettingDao.insert(new OrderSetting(orderDate, number));
        }
    }
}
