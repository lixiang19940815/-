package com.itheima.health.service;

import com.itheima.health.common.ValidateCodeType;

/**
 * 短信SERVICE
 */
public interface SmsService {
    /**
     * 发送短信验证码
     * @param validateCodeType 验证码类型
     * @param phone 手机号
     */
    void sendValidateCode(ValidateCodeType validateCodeType, String phone);

    /**
     *校验短信验证码
     * @param validateCodeType
     * @param phone
     * @param code
     */
    boolean checkValidateCode(ValidateCodeType validateCodeType,String phone,String code);


}
