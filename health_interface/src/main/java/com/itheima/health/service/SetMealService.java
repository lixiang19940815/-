package com.itheima.health.service;

import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.pojo.Setmeal;

import java.util.List;

/**
 * @author zhangmeng
 * @description 套餐SERVICE
 * @date 2019/9/26
 **/
public interface SetMealService {

    /**
     * 添加套餐
     * @param setmeal
     * @param checkgroupIds
     */
    void add(Setmeal setmeal, Integer[] checkgroupIds);

    /**
     * 分页查询
     * @param queryPageBean
     * @return
     */
    PageResult findPage(QueryPageBean queryPageBean);

    /**
     * 查询所有
     * @return
     */
    List<Setmeal> findAll();

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    Setmeal findById(Integer id);
}
