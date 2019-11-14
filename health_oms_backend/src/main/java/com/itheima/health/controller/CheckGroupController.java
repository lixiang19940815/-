package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.common.MessageConst;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.CheckGroup;
import com.itheima.health.service.CheckGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * @author zhangmeng
 * @description 检查组控制器
 * @date 2019/9/18
 **/
@RestController
@RequestMapping("/checkgroup")
@Slf4j
public class CheckGroupController {

    @Reference
    private CheckGroupService checkGroupService;

    /**
     * 添加
     *
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody CheckGroup checkGroup, Integer[] checkitemIds) {
        log.info("[检查组-添加]data:{},checkitemIds:{}", checkGroup, checkitemIds);

        try {
            //RPC请求添加
            checkGroupService.add(checkGroup, checkitemIds);
            return new Result(true, MessageConst.ADD_CHECKGROUP_SUCCESS);
        } catch (RuntimeException e) {
            log.error("[检查组-添加]失败", e);
            return new Result(false, MessageConst.ADD_CHECKGROUP_FAIL + ":" + e.getMessage());
        }
    }


    /**
     * 分页查询
     *
     * @param queryPageBean
     * @return
     */
    @GetMapping("findPage")
    public PageResult findPage(QueryPageBean queryPageBean) {
        log.info("[检查组-分页查询]data:{}", queryPageBean);

        try {
            return checkGroupService.pageQuery(queryPageBean);
        } catch (RuntimeException e) {
            log.error("[检查组-分页查询]", e);
            return new PageResult(0L, Collections.emptyList());
        }
    }

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    @RequestMapping("/findById")
    public Result findById(Integer id) {
        log.info("[检查组-根据id查询]id:{}", id);
        try {
            //rpc 调用业务处理
            CheckGroup checkGroup = checkGroupService.findById(id);
            //封装返回值
            return new Result(true, MessageConst.QUERY_CHECKGROUP_SUCCESS, checkGroup);
        } catch (RuntimeException e) {
            log.error("[检查组-根据id查询]失败", e);
            return new Result(true, MessageConst.QUERY_CHECKGROUP_FAIL + ":" + e.getMessage());
        }
    }

    /**
     * 根据检查组ID查询关联的检查项ID集和
     *
     * @param id
     * @return
     */
    @RequestMapping("/findCheckItemIdsByCheckGroupId")
    public Result findCheckItemIdsByCheckGroupId(Integer id) {
        log.info("[检查组-根据检查组ID查询关联的检查项ID集和]id:{}", id);
        try {
            //rpc 调用业务处理
            List<Integer> itemIds = checkGroupService.findCheckItemIdsByCheckGroupId(id);
            //封装返回值
            return new Result(true, MessageConst.ACTION_SUCCESS, itemIds);
        } catch (RuntimeException e) {
            log.error("[检查组-根据检查组ID查询关联的检查项ID集和]失败", e);
            return new Result(true, MessageConst.ACTION_FAIL + ":" + e.getMessage());
        }
    }

    /**
     * 编辑
     *
     * @return
     */
    @RequestMapping("/edit")
    public Result edit(@RequestBody CheckGroup checkGroup, Integer[] checkitemIds) {
        log.info("[检查组-编辑]data:{},checkitemIds:{}", checkGroup, checkitemIds);

        try {
            //RPC请求添加
            checkGroupService.edit(checkGroup, checkitemIds);
            return new Result(true, MessageConst.EDIT_CHECKGROUP_SUCCESS);
        } catch (RuntimeException e) {
            log.error("[检查组-编辑]失败", e);
            return new Result(false, MessageConst.EDIT_CHECKGROUP_FAIL + ":" + e.getMessage());
        }
    }

    /**
     * 查询所有
     *
     * @return
     */
    @RequestMapping("findAll")
    public Result findAll() {
        try {
            //rpc调用查询数据
            List<CheckGroup> checkGroups = checkGroupService.findAll();
            //封装返回结果
            return new Result(true, MessageConst.QUERY_CHECKGROUP_SUCCESS, checkGroups);
        } catch (RuntimeException e) {
            log.error("",e);
            return new Result(false,MessageConst.QUERY_CHECKGROUP_FAIL+":"+e.getMessage());
        }
    }
}
