package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.itheima.health.common.ValidateCodeType;
import com.itheima.health.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import java.security.SecureRandom;

/**
 * 短信服务实现类
 */
@Service(interfaceClass = SmsService.class)
@Slf4j
public class SmsServiceImpl implements SmsService {
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private IAcsClient acsClient;

    @Override
    public void sendValidateCode(ValidateCodeType validateCodeType, String phone) {
        log.info("[短信-发送验证码]type:{},phone:{}", validateCodeType, phone);
        // 生成验证码
        int code = (int) (1000 + new SecureRandom().nextDouble() * 8999);
        log.debug("[短信-发送验证码]code:{}", code);
        // 保持到缓存（redis）
        String redisKey = validateCodeType.redisKey(phone);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(redisKey, validateCodeType.getExpireSeconds(), String.valueOf(code));
        }
        //构造请求参数
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", "传智健康项目");
        request.putQueryParameter("TemplateCode", validateCodeType.getTemplateCode());
        request.putQueryParameter("TemplateParam", "	{\"assertCode\":\"" + code + "\"}");
//        try {
//            //发起请求
//            CommonResponse response = acsClient.getCommonResponse(request);
//            log.info("[短信-发送验证码]response:{}", response.getData());
//        } catch (ClientException e) {
//            log.error("[短信-发送验证码]", e);
//        }
    }

    @Override
    public boolean checkValidateCode(ValidateCodeType validateCodeType, String phone, String code) {
        log.info("[短信-校验验证码]type:{},phone:{},code:{}", validateCodeType, phone,code);
        try (Jedis jedis = jedisPool.getResource()) {
            //查询redis里的值
            String expectVal = jedis.get(validateCodeType.redisKey(phone));
            //对比是否匹配
            if (!StringUtils.isEmpty(expectVal) && expectVal.equals(code)) {
                //如果匹配则删除redis并返回true
                jedis.del(validateCodeType.redisKey(phone));
                return true;
            } else {
                //如果不匹配则返回false
                return false;
            }
        }
    }

//    @PostConstruct
//    public  void init(){
//        String phone = "xxxxxxxx";
//        this.sendValidateCode(ValidateCodeType.ORDER_SIGNIN,phone);
//        String code = jedisPool.getResource().get(ValidateCodeType.ORDER_SIGNIN.redisKey(phone));
//        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>{}",this.checkValidateCode(ValidateCodeType.ORDER_SIGNIN,phone,code));
//    }
}
