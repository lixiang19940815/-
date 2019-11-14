package com.itheima.health.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestSecurityController {

    @PreAuthorize("hasAuthority('add')")
    @RequestMapping("/addData")
    public String addData(){
        return "add ok";
    }

    @PreAuthorize("hasAuthority('update')")
    @RequestMapping("/updateData")
    public String updateData(){
        return "update ok";
    }

    @PreAuthorize("hasAuthority('delete')")
    @RequestMapping("/delData")
    public String deleteData(){
        return "delete ok";
    }

    @PreAuthorize("hasAuthority('find')")
    @RequestMapping("/findData")
    public String findAll(){
        return "find ok";
    }
}