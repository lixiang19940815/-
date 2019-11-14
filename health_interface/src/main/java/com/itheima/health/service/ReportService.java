package com.itheima.health.service;

import java.util.Map;

/**
 * 数据报表SERVICE
 */
public interface ReportService {
    /**
     * 获取运营统计数据
     * 返回数据格式
     *      reportDate -> yyyy-MM-dd        //日期
     *      todayNewMember -> number		// 今日新增会员
     *      thisWeekNewMember -> number		// 本周新增会员
     *      thisMonthNewMember -> number	// 本月新增会员
     *      totalMember -> number			//  总会员
     *      todayOrderNumber -> number		// 今日预约人数
     *      thisWeekOrderNumber -> number	// 本周预约人数
     *      thisMonthOrderNumber -> number	// 本月预约人数
     *      todayVisitsNumber -> number		// 今日到诊人数
     *      thisWeekVisitsNumber -> number	// 本周到诊人数
     *      thisMonthVisitsNumber -> number	// 本月到诊人数
     *      hotSetmeal -> [ {"proportion":0.4545,"name":"粉红珍爱(女)升级TM12项筛查体检套餐","setmeal_count":5}]	// 热门套餐
     * @return
     */
    Map<String, Object> getBusinessReportData();
}
