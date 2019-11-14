package com.itheima.health.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 验证码类型枚举类
 */
@AllArgsConstructor
public enum ValidateCodeType {
    //体检预约
    ORDER(RedisConst.VALIDATE_CODE_PREFIX+"orderSignin:","SMS_175060789",60),
    //OMS登录（只是举个例子，没有使用场景）
    OMS_SIGNIN(RedisConst.VALIDATE_CODE_PREFIX+"oms:Signin:","SMS_175060789",60),
    //手机登录
    MOBILE_SIGNIN(RedisConst.VALIDATE_CODE_PREFIX+"oms:Signin:","SMS_175060789",60);

    private String redisKeyPrefix;
    @Getter
    private String templateCode;
    @Getter
    private int expireSeconds;

    public String redisKey(String phone){
        return redisKeyPrefix+phone;
    }

}
