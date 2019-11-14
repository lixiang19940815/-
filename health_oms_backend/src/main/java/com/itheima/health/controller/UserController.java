package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.common.MessageConst;
import com.itheima.health.entity.Result;
import com.itheima.health.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangmeng
 * @description
 * @date 2019/9/6
 **/
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    @RequestMapping("/login")
    public Result login(String userName, String password) {

        boolean success = userService.login(userName, password);
        String message = success ? MessageConst.ACTION_SUCCESS : MessageConst.ACTION_FAIL;
        return new Result(success, message);
    }

    @RequestMapping("/loginSuccess")
    public Result loginSuccess() {
        return new Result(true, MessageConst.LOGIN_SUCCESS);
    }

    @RequestMapping("/loginFail")
    public Result loginFail() {
        return new Result(false, "登录失败");
    }

    /**
     * 获取当前登录用户名
     *
     * @return
     */
    @RequestMapping("/getUsername")
    public Result getUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (null != principal && principal instanceof User) {
            User user = (User) principal;
            return new Result(true, MessageConst.GET_USERNAME_SUCCESS, user.getUsername());
        }
        return new Result(false, MessageConst.GET_USERNAME_FAIL);
    }

}
