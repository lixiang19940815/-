package com.itheima.health.mobile.vo;

import lombok.Data;

@Data
public class SmsLoginParam {
    private String telephone;//手机号
    private String validateCode;//验证码
}
