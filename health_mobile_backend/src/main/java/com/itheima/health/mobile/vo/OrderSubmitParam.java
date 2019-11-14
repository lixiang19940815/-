package com.itheima.health.mobile.vo;

import lombok.Data;

import java.util.Date;

@Data
public class OrderSubmitParam {
    private String name;
    private Integer sex;
    private String telephone;
    private String validateCode;
    private String idCard;
    private Date orderDate;
    private Integer setMealId;
}
