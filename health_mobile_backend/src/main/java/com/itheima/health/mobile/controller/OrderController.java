package com.itheima.health.mobile.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.common.MessageConst;
import com.itheima.health.common.ValidateCodeType;
import com.itheima.health.entity.Result;
import com.itheima.health.exception.DuplicateOperateException;
import com.itheima.health.exception.OrderCountOverLimitExceptoin;
import com.itheima.health.exception.OrderTimeNotAllowedException;
import com.itheima.health.mobile.vo.OrderSubmitParam;
import com.itheima.health.pojo.Member;
import com.itheima.health.pojo.Order;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.MemberService;
import com.itheima.health.service.OrderService;
import com.itheima.health.service.SetMealService;
import com.itheima.health.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 预约控制器
 */
@RestController
@RequestMapping("/mobile/order")
@Slf4j
public class OrderController {

    @Reference
    private SmsService smsService;
    @Reference
    private MemberService memberService;
    @Reference
    private OrderService orderService;
    @Reference
    private SetMealService setMealService;

    /**
     * 提交预约信息<br/>
     * 使用VO而不是Map接收参数的好处：
     *  1-方便统一做参数校验
     *  2-代码更简洁
     *  3-方便使用工具生成API文档
     *
     * 为什么不把信息全部交给provider处理:
     *  1-不同分层有不同的作用，service层不应该过多考虑控制器层的业务问题
     *  2-避免分布式系统的常见问题（超时），后端接口职责尽可能单一，尽快返回结果，避免大接口
     *
     *  为什么exception放在interface包里面：
     *  对于老版本的dubbo，如果exception和接口不在一个jar里面，异常会被转换为RPC异常
     *
     *  正确使用受检异常和运行时异常：
     *  受检异常是第一种正常的业务返回结果，运行时异常是不可控因素（空指针、网络抖动、异常业务逻辑）
     *
     * @param param
     * @return
     */
    @RequestMapping("/submit")
    public Result submit(@RequestBody OrderSubmitParam param) {
        log.info("[提交预约信息]data：{}", param);

        try {
            //1-验证码验证  smsService
            if (!smsService.checkValidateCode(ValidateCodeType.ORDER, param.getTelephone(), param.getValidateCode())) {
                return new Result(false, MessageConst.VALIDATECODE_ERROR);
            }

            //2-创建会员 memberService
            Member member = new Member();
            member.setIdCard(param.getIdCard());
            member.setRegTime(new Date());
            member.setPhoneNumber(param.getTelephone());
            member.setName(param.getName());
            if (null != param.getSex()) {
                member.setSex(param.getSex() == 1 ? "男" : "女");
            }
            try {
                member = memberService.addMember(member);
            } catch (DuplicateOperateException e) {
                log.info("", e);
                member = memberService.findByPhone(member.getPhoneNumber());
            }
            //3-提交体检信息  orderService
            Order order = new Order();
            order.setOrderStatus(Order.ORDERSTATUS_NO);
            order.setSetmealId(param.getSetMealId());
            order.setOrderType(Order.ORDERTYPE_WEIXIN);
            order.setOrderDate(param.getOrderDate());
            order.setMemberId(member.getId());
            try {
                order = orderService.add(order);
            } catch (OrderCountOverLimitExceptoin e) {
                log.info("",e);
                return new Result(false,MessageConst.ORDER_FULL);
            } catch (DuplicateOperateException e) {
                log.info("",e);
                return new Result(false,MessageConst.HAS_ORDERED);
            } catch (OrderTimeNotAllowedException e) {
                log.info("",e);
                return new Result(false,MessageConst.SELECTED_DATE_CANNOT_ORDER);
            }
            //4-返回结果
            return new Result(true, MessageConst.ORDER_SUCCESS,order);
        } catch (RuntimeException e) {
            log.error("",e);
            return new Result(false,MessageConst.ACTION_FAIL);
        }
    }

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    @RequestMapping("/findById")
    public Result findById(Integer id){
        try {
            //rpc查询数据
            Order order = orderService.findById(id);
            Member member = memberService.findById(order.getMemberId());
            Setmeal setmeal = setMealService.findById(order.getSetmealId());

            //封装返回结果
            Map<String,Object> map = new HashMap<>();
            map.put("member",member.getName());
            map.put("setmeal",setmeal.getName());
            map.put("orderDate",new SimpleDateFormat("yyyy-MM-dd").format(order.getOrderDate()));
            map.put("orderType",order.getOrderType());
            return new Result(true,MessageConst.QUERY_ORDER_SUCCESS,map);
        } catch (RuntimeException e) {
            log.error("",e);
            return new Result(false,MessageConst.QUERY_ORDER_FAIL);
        }
    }
}
