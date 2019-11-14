package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.common.MessageConst;
import com.itheima.health.entity.Result;
import com.itheima.health.service.MemberService;
import com.itheima.health.service.OrderService;
import com.itheima.health.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 数据报告控制器
 */
@RestController
@RequestMapping("/report")
@Slf4j
public class ReportController {

    @Reference
    private MemberService memberService;

    @Reference
    private OrderService orderService;

    @Reference
    private ReportService reportService;

    /**
     * 查询用户报告
     *
     * @return
     */
    @RequestMapping("/getMemberReport")
    public Result getMemberReport() {

        try {
            List<String> months = new ArrayList<>();
            List<Integer> memeberCount = null;
            //构造months
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -12);
            DateFormat df = new SimpleDateFormat("yyyy.MM");
            for (int i = 0; i < 12; i++) {
                cal.add(Calendar.MONTH, 1);
                months.add(df.format(cal.getTime()));
            }

            //构造memberCount
            memeberCount = memberService.countByMonth(months);

            Map<String, Object> map = new HashMap<>();
            map.put("months", months);
            map.put("memberCount", memeberCount);
            return new Result(true, MessageConst.GET_MEMBER_NUMBER_REPORT_SUCCESS, map);
        } catch (RuntimeException e) {
            log.error("", e);
            return new Result(false, MessageConst.GET_MEMBER_NUMBER_REPORT_FAIL);

        }
    }

    /**
     * 查询套餐预约统计报告
     * 返回值数据格式
     * {
     * "data":{
     * "setmealNames":["套餐1","套餐2","套餐3"],
     * "setmealCount":[
     * {"name":"套餐1","value":10},
     * {"name":"套餐2","value":30},
     * {"name":"套餐3","value":25}
     * ]
     * },
     * "flag":true,
     * "message":"获取套餐统计数据成功"
     * }
     *
     * @return
     */
    @GetMapping("/getSetmealReport")
    public Result getSetmealReport() {

        try {
            //获取数据
            List<Map<String, Object>> setmealCount = orderService.countBySetmeal();
            List<String> setmealNames = new ArrayList<>();
            for (Map<String, Object> map : setmealCount) {
                setmealNames.add(String.valueOf(map.get("name")));
            }

            //构造返回值
            Map<String, Object> map = new HashMap<>();
            map.put("setmealNames", setmealNames);
            map.put("setmealCount", setmealCount);
            return new Result(true, MessageConst.GET_SETMEAL_COUNT_REPORT_SUCCESS, map);
        } catch (RuntimeException e) {
            log.error("", e);
            return new Result(false, MessageConst.GET_SETMEAL_COUNT_REPORT_FAIL);
        }
    }

    /**
     * 查询运营统计数据
     * 返回数据格式：
     * {
     * "data":{
     * "todayVisitsNumber":0,
     * "reportDate":"2019-04-25",
     * "todayNewMember":0,
     * "thisWeekVisitsNumber":0,
     * "thisMonthNewMember":2,
     * "thisWeekNewMember":0,
     * "totalMember":10,
     * "thisMonthOrderNumber":2,
     * "thisMonthVisitsNumber":0,
     * "todayOrderNumber":0,
     * "thisWeekOrderNumber":0,
     * "hotSetmeal":[
     * {"proportion":0.4545,"name":"粉红珍爱(女)升级TM12项筛查体检套餐","setmeal_count":5},
     * {"proportion":0.1818,"name":"阳光爸妈升级肿瘤12项筛查体检套餐","setmeal_count":2},
     * {"proportion":0.1818,"name":"珍爱高端升级肿瘤12项筛查","setmeal_count":2},
     * {"proportion":0.0909,"name":"孕前检查套餐","setmeal_count":1}
     * ],
     * },
     * "flag":true,
     * "message":"获取运营统计数据成功"
     * }
     *
     * @return
     */
    @RequestMapping("/getBusinessReportData")
    public Result getBusinessReportData() {
        try {
            Map<String, Object> map = reportService.getBusinessReportData();
            return new Result(true, MessageConst.GET_BUSINESS_REPORT_SUCCESS, map);
        } catch (RuntimeException e) {
            log.error("", e);
            return new Result(false, MessageConst.GET_BUSINESS_REPORT_FAIL);
        }
    }

    /**
     * 导出运营数据报表
     *
     * @return
     */
    @GetMapping("exportBusinessReport")
    public Result exportBusinessReport(HttpServletResponse response) {
        log.info("[导出运营数据报表]开始");
        try {
            //1-RPC获取数据
            Map<String, Object> reportData = reportService.getBusinessReportData();
            log.debug("[导出运营数据报表]rpc rsp:{}", reportData);
            // 取出返回结果
            String reportDate = (String) reportData.get("reportDate");
            // 获取会员相关数据
            Long todayNewMember = (Long) reportData.get("todayNewMember");
            Long thisWeekNewMember = (Long) reportData.get("thisWeekNewMember");
            Long thisMonthNewMember = (Long) reportData.get("thisMonthNewMember");
            Long totalMember = (Long) reportData.get("totalMember");
            // 获取预约相关数据
            Long todayOrderNumber = (Long) reportData.get("todayOrderNumber");
            Long thisWeekOrderNumber = (Long) reportData.get("thisWeekOrderNumber");
            Long thisMonthOrderNumber = (Long) reportData.get("thisMonthOrderNumber");
            // 获取到诊相关数据
            Long todayVisitsNumber = (Long) reportData.get("todayVisitsNumber");
            Long thisWeekVisitsNumber = (Long) reportData.get("thisWeekVisitsNumber");
            Long thisMonthVisitsNumber = (Long) reportData.get("thisMonthVisitsNumber");
            // 获取套餐数据
            List<Map> hotSetmeal = (List<Map>) reportData.get("hotSetmeal");

            //2-读取模板构造workBook
            try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("report_template.xlsx");
                 Workbook workbook = new XSSFWorkbook(is);
                 OutputStream os = response.getOutputStream()) {
                Sheet sheet = workbook.getSheetAt(0);
                //3-填入数据
                //日期
                sheet.getRow(2).getCell(5).setCellValue(reportDate);
                //会员统计数据
                sheet.getRow(4).getCell(5).setCellValue(todayNewMember);
                sheet.getRow(4).getCell(7).setCellValue(totalMember);
                sheet.getRow(5).getCell(5).setCellValue(thisWeekNewMember);
                sheet.getRow(5).getCell(7).setCellValue(thisMonthNewMember);
                //预约到诊数据统计
                sheet.getRow(7).getCell(5).setCellValue(todayOrderNumber);
                sheet.getRow(7).getCell(7).setCellValue(todayVisitsNumber);
                sheet.getRow(8).getCell(5).setCellValue(thisWeekOrderNumber);
                sheet.getRow(8).getCell(7).setCellValue(thisWeekVisitsNumber);
                sheet.getRow(9).getCell(5).setCellValue(thisMonthOrderNumber);
                sheet.getRow(9).getCell(7).setCellValue(thisMonthVisitsNumber);
                //热门套餐
                int rowNum = 12;
                for (Map setMeal : hotSetmeal) {
                    Row row = sheet.getRow(rowNum);
                    row.getCell(4).setCellValue((String) setMeal.get("name"));
                    row.getCell(5).setCellValue((Long) setMeal.get("setmeal_count"));
                    row.getCell(6).setCellValue(((BigDecimal) setMeal.get("proportion")).doubleValue());
                    rowNum++;
                }

                //4-写入网络输出流
                //写入返回流
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("content-Disposition", "attachment;fileName=" + reportDate + "_report.xlsx");
                workbook.write(os);
                return null;
            } catch (IOException e) {
                // 不会出现/无法处理的受检异常，转换为运行时异常
                throw new RuntimeException(e);
            }
        } catch (RuntimeException e) {
            log.error("", e);
            return new Result(false, MessageConst.ACTION_FAIL);
        }


    }

}
