package com.itheima.health.service;

import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.pojo.CheckItem;

import java.util.List;

/**
 * @author zhangmeng
 * @description 检查项Service
 * @date 2019/9/16
 **/
public interface CheckItemService {
    /**
     * 添加
     * @param checkItem 检查项
     */
    void add(CheckItem checkItem);

    /**
     * 分页查询
     * @param queryPageBean
     * @return
     */
    PageResult pageQuery(QueryPageBean queryPageBean);

    /**
     * 根据id删除
     * @param id 数据id
     */
    void delete(int id);

    /**
     * 编辑
     * @param checkItem 检查组
     */
    void edit(CheckItem checkItem);

    /**
     * 根据ID查询
     * @param id 数据id
     * @return
     */
    CheckItem findById(Integer id);

    /**
     * 查询所有
     * @return
     */
    List<CheckItem> findAll();
}
