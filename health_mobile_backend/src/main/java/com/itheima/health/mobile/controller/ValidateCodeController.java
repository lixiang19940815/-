package com.itheima.health.mobile.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.common.MessageConst;
import com.itheima.health.common.ValidateCodeType;
import com.itheima.health.entity.Result;
import com.itheima.health.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证码Controller
 */
@RestController
@RequestMapping("/mobile/validateCode")
@Slf4j
public class ValidateCodeController {

    @Reference
    private SmsService smsService;
    /**
     * 发送验证码
     * @param type 验证码类型
     * @param telephone 手机号
     * @return
     */
    @PostMapping("/send")
    public Result send(ValidateCodeType type,String telephone){
        log.info("[验证码-发送]type:{},telephone:{}",type,telephone);
        try {
            //rpc调用发送验证码
            smsService.sendValidateCode(type,telephone);
            return new Result(true, MessageConst.SEND_VALIDATECODE_SUCCESS);
        } catch (RuntimeException e) {
            log.error("",e);
            return new Result(false,MessageConst.SEND_VALIDATECODE_FAIL);
        }
    }
}
