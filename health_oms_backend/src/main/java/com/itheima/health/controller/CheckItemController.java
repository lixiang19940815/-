package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.common.MessageConst;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.CheckGroup;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.service.CheckItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * @author zhangmeng
 * @description 检查项Controller
 * @date 2019/9/16
 **/
@RestController
@RequestMapping("/checkitem")
@Slf4j
public class CheckItemController {
    @Reference
    private CheckItemService checkItemService;

    /**
     * 新增
     * 1、rpc调用完成新增业务
     * 2、返回结果
     *
     * @param checkItem
     * @return
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('CHECKITEM_ADD')")
    public Result add(@RequestBody CheckItem checkItem) {
        log.info("[检查项-新增]data:{}", checkItem);
        try {
            checkItemService.add(checkItem);
            return new Result(true, MessageConst.ADD_CHECKITEM_SUCCESS);
        } catch (RuntimeException e) {
            log.error("[检查项-新增]", e);
            return new Result(false, MessageConst.ADD_CHECKITEM_FAIL + "：" + e.getMessage());
        }
    }

    /**
     * 分页查询
     *
     * @param queryPageBean
     * @return
     */
    @GetMapping("findPage")
    @PreAuthorize("hasAuthority('CHECKITEM_QUERY')")
    public PageResult findPage(QueryPageBean queryPageBean) {
        log.info("[检查项-分页查询]data:{}", queryPageBean);
        try {
            return checkItemService.pageQuery(queryPageBean);
        } catch (RuntimeException e) {
            log.error("[检查项-分页查询]异常", e);
            return new PageResult(0L, Collections.emptyList());
        }
    }

    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    @PreAuthorize("hasAuthority('CHECKITEM_DELETE')")
    public Result delete(Integer id) {
        log.info("[检查项-根据id删除]id：{}", id);
        try {
            //RPC调用处理业务
            checkItemService.delete(id);
            return new Result(true, MessageConst.DELETE_CHECKITEM_SUCCESS);
        } catch (RuntimeException e) {
            log.error("[检查项-根据id删除]", e);
            return new Result(false, MessageConst.DELETE_CHECKITEM_FAIL + ":" + e.getMessage());
        }

    }

    /**
     * 编辑
     *
     * @param checkItem
     * @return
     */
    @RequestMapping("/edit")
    @PreAuthorize("hasAuthority('CHECKITEM_EDIT')")
    public Result edit(@RequestBody CheckItem checkItem) {
        log.info("[检查项-编辑]data:", checkItem);
        try {
            checkItemService.edit(checkItem);
            return new Result(true, MessageConst.EDIT_CHECKITEM_SUCCESS);
        } catch (RuntimeException e) {
            log.error("[检查项-编辑]", e);
            return new Result(false, MessageConst.EDIT_CHECKITEM_FAIL + ":" + e.getMessage());
        }
    }

    /**
     * 根据Id查询
     *
     * @param id
     * @return
     */
    @RequestMapping("/findById")
    @PreAuthorize("hasAuthority('CHECKITEM_QUERY')")
    public Result findById(Integer id) {
        log.info("[检查项-根据ID查询]id:{}", id);
        try {
            CheckItem checkItem = checkItemService.findById(id);
            return new Result(true, MessageConst.ACTION_SUCCESS, checkItem);
        } catch (RuntimeException e) {
            log.error("[检查项-根据ID查询]异常", e);
            return new Result(false, MessageConst.ACTION_FAIL + ":" + e.getMessage());
        }
    }

    /**
     * 查询所有检查项
     * @return
     */
    @RequestMapping("/findAll")
    @PreAuthorize("hasAuthority('CHECKITEM_QUERY')")
    public Result findAll(){
        log.info("[检查项-查询所有]~");
        try {
            List<CheckItem> checkItems = checkItemService.findAll();
            return new Result(true,MessageConst.QUERY_CHECKITEM_SUCCESS,checkItems);
        } catch (RuntimeException e) {
            log.error("[检查项-查询所有]异常",e);
            return new Result(false,MessageConst.EDIT_CHECKITEM_FAIL+":"+e.getMessage());
        }
    }
}
