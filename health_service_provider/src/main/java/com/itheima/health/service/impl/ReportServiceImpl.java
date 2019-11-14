package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.MemberDao;
import com.itheima.health.dao.OrderDao;
import com.itheima.health.pojo.Order;
import com.itheima.health.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据报表service
 */
@Service(interfaceClass = ReportService.class)
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private OrderDao orderDao;

    @Override
    public Map<String, Object> getBusinessReportData() {
        Map<String, Object> resultMap = new HashMap<>();

        //日期处理
        Calendar today = Calendar.getInstance();
        //将时、分、秒、毫毛清零
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        //设置周一为每周第一天
        today.setFirstDayOfWeek(Calendar.MONDAY);
        //本周第一天
        Calendar weekFirstDay = (Calendar) today.clone();
        weekFirstDay.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        //本月第一天
        Calendar monthFirstDay = (Calendar) today.clone();
        monthFirstDay.set(Calendar.DAY_OF_MONTH, 1);


        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        //会员数据 memberDao
        //      reportDate -> yyyy-MM-dd        //日期
        resultMap.put("reportDate", df.format(today.getTime()));
        //      todayNewMember -> number		// 今日新增会员
        resultMap.put("todayNewMember", memberDao.countByRegTimeRange(today.getTime(), today.getTime()));
        //      thisWeekNewMember -> number		// 本周新增会员
        resultMap.put("thisWeekNewMember", memberDao.countByRegTimeRange(weekFirstDay.getTime(), today.getTime()));
        //      thisMonthNewMember -> number	// 本月新增会员
        resultMap.put("thisMonthNewMember", memberDao.countByRegTimeRange(monthFirstDay.getTime(), today.getTime()));
        //      totalMember -> number			//  总会员
        resultMap.put("totalMember", memberDao.countByRegTimeBefore(df.format(today.getTime())));

        //预约数据 orderDao
        //      todayOrderNumber -> number		// 今日预约人数
        resultMap.put("todayOrderNumber", orderDao.countByOrderDateRange(today.getTime(), today.getTime(), null));
        //      thisWeekOrderNumber -> number	// 本周预约人数
        resultMap.put("thisWeekOrderNumber", orderDao.countByOrderDateRange(weekFirstDay.getTime(), today.getTime(), null));
        //      thisMonthOrderNumber -> number	// 本月预约人数
        resultMap.put("thisMonthOrderNumber", orderDao.countByOrderDateRange(monthFirstDay.getTime(), today.getTime(), null));
        //      todayVisitsNumber -> number		// 今日到诊人数
        resultMap.put("todayVisitsNumber", orderDao.countByOrderDateRange(today.getTime(), today.getTime(), Order.ORDERSTATUS_YES));
        //      thisWeekVisitsNumber -> number	// 本周到诊人数
        resultMap.put("thisWeekVisitsNumber", orderDao.countByOrderDateRange(weekFirstDay.getTime(), today.getTime(), Order.ORDERSTATUS_YES));
        //      thisMonthVisitsNumber -> number	// 本月到诊人数
        resultMap.put("thisMonthVisitsNumber", orderDao.countByOrderDateRange(monthFirstDay.getTime(), today.getTime(), Order.ORDERSTATUS_YES));

        //热门套餐
        //      hotSetmeals ->  [ {"proportion":0.4545,"name":"粉红珍爱(女)升级TM12项筛查体检套餐","setmeal_count":5}]	// 热门套餐
        resultMap.put("hotSetmeal", orderDao.countByHotSetMeal(4));

        return resultMap;
    }
}
