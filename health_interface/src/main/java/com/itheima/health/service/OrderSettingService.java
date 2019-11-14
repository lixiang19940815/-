package com.itheima.health.service;

import com.itheima.health.pojo.OrderSetting;

import java.util.Date;
import java.util.List;

/**
 * @author zhangmeng
 * @description 预约设置SERVICE
 * @date 2019/9/29
 **/
public interface OrderSettingService {

    /**
     * 批量添加
     * @param orderSettings
     */
    void addAll(List<OrderSetting> orderSettings);

    /**
     * 根据月份查询数据
     * @param month
     * @param year
     * @return
     */
    List<OrderSetting> getOrderSettingByMonth(int year, int month);
    /**
     * 根据日期编辑number
     * @param orderDate
     * @param number
     */
    void editNumberByDate(Date orderDate, int number);
}
