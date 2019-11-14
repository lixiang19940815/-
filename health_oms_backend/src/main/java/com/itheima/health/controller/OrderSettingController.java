package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.common.MessageConst;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.OrderSetting;
import com.itheima.health.service.OrderSettingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author zhangmeng
 * @description 预约设置控制器
 * @date 2019/9/29
 **/
@RestController
@RequestMapping("/ordersetting")
@Slf4j
public class OrderSettingController {
    @Reference
    private OrderSettingService orderSettingService;
    /**
     * 上传预约设置的excel文件
     * @param multipartFile
     * @return
     */
    @RequestMapping("/upload")
    public Result upload(@RequestParam("excelFile") MultipartFile multipartFile) {
        log.info("[预约设置-上传]fileName:{},size:{}", multipartFile.getOriginalFilename(), multipartFile.getSize());
        String filename = multipartFile.getOriginalFilename();
        if(StringUtils.isEmpty(filename)){
            return new Result(false,"缺少文件名");
        }
        //1 抽取excel数据 poi

        try (InputStream is = multipartFile.getInputStream();) {
            Workbook workbook = null;
            //1.1构造workbook
            if(filename.endsWith(".xls")){
                // excel 2003
                workbook = new HSSFWorkbook(is);
            }else if(filename.endsWith(".xlsx")){
                // excel 2007
                workbook = new XSSFWorkbook(is);
            }else{
                return new Result(false,"文件格式不正确，请检查重试");
            }

            //1.2遍历取数据
            List<OrderSetting> orderSettings = new ArrayList<>();
            for (Sheet sheet : workbook) {
                // 第0行为表头，直接从第1行取
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    Date date = null;
                    int number = 0;
                    try {
                        date = null != row.getCell(0) ? row.getCell(0).getDateCellValue() : null;
                        number = null != row.getCell(1) ? Double.valueOf(row.getCell(1).getNumericCellValue()).intValue() : 0;
                    }catch (IllegalStateException|NumberFormatException e){
                        log.error("",e);
                        return new Result(false,String.format("数据格式错误,%s 第%d行",sheet.getSheetName(),row.getRowNum()+1));
                    }
                    if(null == date ){
                        return new Result(false,String.format("缺少必填数据,%s 第%d行",sheet.getSheetName(),row.getRowNum()+1));
                    }
                    //构造OrderSetting
                    orderSettings.add(new OrderSetting(date,number));
                }
            }
            log.info("[预约设置-上传]解析成功,result:{}",orderSettings);

            //2 rpc调用数据入库
            orderSettingService.addAll(orderSettings);
            return new Result(true,MessageConst.IMPORT_ORDERSETTING_SUCCESS);
        } catch (RuntimeException|IOException e) {
            log.info("",e);
            return new Result(false, MessageConst.IMPORT_ORDERSETTING_FAIL);
        }
    }

    /**
     * 根据月份查询数据
     * @param year 年
     * @param month 月
     * @return
     */
    @GetMapping("/getOrderSettingByMonth")
    public Result getOrderSettingByMonth( @RequestParam Integer year,@RequestParam Integer month){
        try {
            //调用RPC查询数据
            List<OrderSetting> orderSettings = orderSettingService.getOrderSettingByMonth(year,month);
            //转换成前端需要的结构
            List<Map<String,Object>> voList = new ArrayList<>();
            for (OrderSetting orderSetting : orderSettings) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(orderSetting.getOrderDate());
                Map<String,Object> map = new HashMap<>();
                map.put("date",cal.get(Calendar.DAY_OF_MONTH));
                map.put("number",orderSetting.getNumber());
                map.put("reservations",orderSetting.getReservations());
                voList.add(map);
            }
            return new Result(true,MessageConst.GET_ORDERSETTING_SUCCESS,voList);
        } catch (RuntimeException e) {
            log.error("",e);
            return new Result(false,MessageConst.GET_ORDERSETTING_FAIL);
        }
    }
    /**
     * 根据日期编辑
     * @param orderSetting
     * @return
     */
    @RequestMapping("/editNumberByDate")
    public Result editNumberByDate(@RequestBody OrderSetting orderSetting){
        log.info("[预约设置-根据日期编辑]data:{}",orderSetting);
        try {
            orderSettingService.editNumberByDate(orderSetting.getOrderDate(),orderSetting.getNumber());
            return new Result(true,MessageConst.ORDERSETTING_SUCCESS);
        } catch (RuntimeException e) {
            log.error("",e);
            return new Result(false,MessageConst.ORDERSETTING_FAIL);
        }
    }
}
