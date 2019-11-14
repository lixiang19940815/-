package com.itheima.health.mobile.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.common.MessageConst;
import com.itheima.health.common.ValidateCodeType;
import com.itheima.health.entity.Result;
import com.itheima.health.exception.DuplicateOperateException;
import com.itheima.health.mobile.vo.SmsLoginParam;
import com.itheima.health.pojo.Member;
import com.itheima.health.service.MemberService;
import com.itheima.health.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 登录控制器
 */
@RestController
@RequestMapping("/mobile/login")
@Slf4j
public class LoginController {
    @Reference
    private SmsService smsService;
    @Reference
    private MemberService memberService;
    @PostMapping("smsLogin")
    public Result login(@RequestBody SmsLoginParam param){
        try {
            //验证码校验
            boolean success = smsService.checkValidateCode(ValidateCodeType.MOBILE_SIGNIN, param.getTelephone(), param.getValidateCode());
            if (!success) {
                return new Result(false, MessageConst.VALIDATECODE_ERROR);
            }

            //创建用户
            Member member = new Member();
            member.setPhoneNumber(param.getTelephone());
            member.setRegTime(new Date());
            try {
                member = memberService.addMember(member);
            } catch (DuplicateOperateException e) {
                log.info("", e);
                member = memberService.findByPhone(member.getPhoneNumber());
            }
            //登录（模拟，直接输出成功）
            log.info("[登录成功]》》》》》》》》》》》》》》》》》》》》》》》》{}", member.getId());
            return new Result(true, MessageConst.LOGIN_SUCCESS);
        }catch (RuntimeException e){
            log.error("",e);
            return new Result(false,MessageConst.ACTION_FAIL);
        }
    }
}
